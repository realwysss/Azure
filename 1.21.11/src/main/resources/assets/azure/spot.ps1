param([string]$cover, [int]$ppid = 0)
$ErrorActionPreference = 'Continue'
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
Add-Type -AssemblyName System.Runtime.WindowsRuntime
Add-Type -TypeDefinition @"
using System;
using System.Runtime.InteropServices;
using System.Diagnostics;
using System.Collections.Generic;

namespace Az {
  [ComImport, Guid("BCDE0395-E52F-467C-8E3D-C4579291692E")]
  public class MMDeviceEnumeratorCom { }

  [ComImport, Guid("A95664D2-9614-4F35-A746-DE8DB63617E6"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
  public interface IMMDeviceEnumerator {
    [PreserveSig] int EnumAudioEndpoints(int dataFlow, int stateMask, out IntPtr devices);
    [PreserveSig] int GetDefaultAudioEndpoint(int dataFlow, int role, out IMMDevice endpoint);
  }

  [ComImport, Guid("D666063F-1587-4E43-81F1-B948E807363F"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
  public interface IMMDevice {
    [PreserveSig] int Activate(ref Guid iid, int clsCtx, IntPtr activationParams, [MarshalAs(UnmanagedType.IUnknown)] out object iface);
  }

  [ComImport, Guid("77AA99A0-1BD6-484F-8BC7-2C654C9A9B6F"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
  public interface IAudioSessionManager2 {
    [PreserveSig] int GetAudioSessionControl(IntPtr a, int b, out IntPtr c);
    [PreserveSig] int GetSimpleAudioVolume(IntPtr a, int b, out IntPtr c);
    [PreserveSig] int GetSessionEnumerator(out IAudioSessionEnumerator e);
  }

  [ComImport, Guid("E2F5BB11-0570-40CA-ACDD-3AA01277DEE8"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
  public interface IAudioSessionEnumerator {
    [PreserveSig] int GetCount(out int count);
    [PreserveSig] int GetSession(int index, out IAudioSessionControl2 session);
  }

  [ComImport, Guid("bfb7ff88-7239-4fc9-8fa2-07c950be9c6d"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
  public interface IAudioSessionControl2 {
    [PreserveSig] int GetState(out int s);
    [PreserveSig] int GetDisplayName(out IntPtr s);
    [PreserveSig] int SetDisplayName(IntPtr s, IntPtr c);
    [PreserveSig] int GetIconPath(out IntPtr s);
    [PreserveSig] int SetIconPath(IntPtr s, IntPtr c);
    [PreserveSig] int GetGroupingParam(out Guid g);
    [PreserveSig] int SetGroupingParam(ref Guid g, IntPtr c);
    [PreserveSig] int RegisterAudioSessionNotification(IntPtr n);
    [PreserveSig] int UnregisterAudioSessionNotification(IntPtr n);
    [PreserveSig] int GetSessionIdentifier(out IntPtr s);
    [PreserveSig] int GetSessionInstanceIdentifier(out IntPtr s);
    [PreserveSig] int GetProcessId(out uint pid);
  }

  [ComImport, Guid("C02216F6-8C67-4B5B-9D00-D008E73E0064"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
  public interface IAudioMeterInformation {
    [PreserveSig] int GetPeakValue(out float peak);
  }

  public static class Meter {
    static IAudioSessionManager2 mgr;

    static void Init() {
      var en = (IMMDeviceEnumerator)(new MMDeviceEnumeratorCom());
      IMMDevice dev;
      en.GetDefaultAudioEndpoint(0, 1, out dev);
      Guid iid = new Guid("77AA99A0-1BD6-484F-8BC7-2C654C9A9B6F");
      object o;
      dev.Activate(ref iid, 23, IntPtr.Zero, out o);
      mgr = (IAudioSessionManager2)o;
    }

    public static float Peak(string procName) {
      try {
        if (mgr == null) Init();
        IAudioSessionEnumerator se;
        mgr.GetSessionEnumerator(out se);
        int n;
        se.GetCount(out n);
        float best = 0f;
        for (int i = 0; i < n; i++) {
          IAudioSessionControl2 c;
          se.GetSession(i, out c);
          uint pid;
          c.GetProcessId(out pid);
          if (pid == 0) continue;
          string name = "";
          try { name = Process.GetProcessById((int)pid).ProcessName; } catch { }
          if (!name.Equals(procName, StringComparison.OrdinalIgnoreCase)) continue;
          float p;
          ((IAudioMeterInformation)c).GetPeakValue(out p);
          if (p > best) best = p;
        }
        return best;
      } catch {
        mgr = null;
        return -1f;
      }
    }
  }
}
"@

$asTaskGeneric = ([System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object { $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1' })[0]
function Await($op, $type) {
  $m = $asTaskGeneric.MakeGenericMethod($type)
  $t = $m.Invoke($null, @($op))
  $t.Wait(-1) | Out-Null
  $t.Result
}
[void][Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager, Windows.Media.Control, ContentType = WindowsRuntime]
[void][Windows.Storage.Streams.IRandomAccessStreamWithContentType, Windows.Storage.Streams, ContentType = WindowsRuntime]
[void][Windows.Storage.Streams.IInputStream, Windows.Storage.Streams, ContentType = WindowsRuntime]
$mgr = Await ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager]::RequestAsync()) ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager])
$asRead = [System.IO.WindowsRuntimeStreamExtensions].GetMethods() | Where-Object { $_.Name -eq 'AsStreamForRead' -and $_.GetParameters().Count -eq 1 } | Select-Object -First 1
$lastTitle = ''
$tick = 0
$out = [Console]::Out
while ($true) {
  if ($tick % 8 -eq 0) {
    $info = $null
    foreach ($s in $mgr.GetSessions()) {
      if ($s.SourceAppUserModelId -like '*Spotify*') { $info = $s; break }
    }
    if ($info -ne $null) {
      $props = Await ($info.TryGetMediaPropertiesAsync()) ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties])
      $tl = $info.GetTimelineProperties()
      $pb = $info.GetPlaybackInfo()
      $playing = 0
      if ($pb.PlaybackStatus -eq 'Playing') { $playing = 1 }
      $key = $props.Title + '|' + $props.Artist
      if ($key -ne $lastTitle -and $props.Thumbnail -ne $null) {
        $lastTitle = $key
        try {
          $st = Await ($props.Thumbnail.OpenReadAsync()) ([Windows.Storage.Streams.IRandomAccessStreamWithContentType])
          $rs = $asRead.Invoke($null, @($st))
          $fs = [System.IO.File]::Create($cover)
          $rs.CopyTo($fs)
          $fs.Close()
          $rs.Close()
          $out.WriteLine("C`t" + [DateTime]::Now.Ticks)
        } catch { $out.WriteLine("E`t" + $_.Exception.Message) }
      }
      $pos = [int64]$tl.Position.TotalMilliseconds
      $end = [int64]$tl.EndTime.TotalMilliseconds
      if ($playing -eq 1) {
        $age = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds() - [int64]$tl.LastUpdatedTime.ToUnixTimeMilliseconds()
        if ($age -gt 0 -and $age -lt 600000) { $pos += $age }
      }
      if ($end -gt 0 -and $pos -gt $end) { $pos = $end }
      $out.WriteLine("I`t$playing`t$pos`t$end`t" + $props.Title + "`t" + $props.Artist)
    } else {
      $out.WriteLine("N")
    }
    $out.Flush()
  }
  if ($tick % 8 -eq 0 -and $ppid -gt 0 -and -not (Get-Process -Id $ppid -ErrorAction SilentlyContinue)) { exit }
  $lv = [Az.Meter]::Peak('Spotify')
  $out.WriteLine("L`t" + ([float]$lv).ToString('0.000', [Globalization.CultureInfo]::InvariantCulture))
  $out.Flush()
  $tick++
  Start-Sleep -Milliseconds 120
}
