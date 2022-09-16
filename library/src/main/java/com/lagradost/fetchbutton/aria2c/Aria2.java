package com.lagradost.fetchbutton.aria2c;


//https://github.com/devgianlu/aria2lib/blob/f23de9515adef87ea3ad0268cf1929c9de1bce23/src/main/java/com/gianlu/aria2lib/internal/Aria2.java
/*                                 Apache License
                           Version 2.0, January 2004
                        http://www.apache.org/licenses/

   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION

   1. Definitions.

      "License" shall mean the terms and conditions for use, reproduction,
      and distribution as defined by Sections 1 through 9 of this document.

      "Licensor" shall mean the copyright owner or entity authorized by
      the copyright owner that is granting the License.

      "Legal Entity" shall mean the union of the acting entity and all
      other entities that control, are controlled by, or are under common
      control with that entity. For the purposes of this definition,
      "control" means (i) the power, direct or indirect, to cause the
      direction or management of such entity, whether by contract or
      otherwise, or (ii) ownership of fifty percent (50%) or more of the
      outstanding shares, or (iii) beneficial ownership of such entity.

      "You" (or "Your") shall mean an individual or Legal Entity
      exercising permissions granted by this License.

      "Source" form shall mean the preferred form for making modifications,
      including but not limited to software source code, documentation
      source, and configuration files.

      "Object" form shall mean any form resulting from mechanical
      transformation or translation of a Source form, including but
      not limited to compiled object code, generated documentation,
      and conversions to other media types.

      "Work" shall mean the work of authorship, whether in Source or
      Object form, made available under the License, as indicated by a
      copyright notice that is included in or attached to the work
      (an example is provided in the Appendix below).

      "Derivative Works" shall mean any work, whether in Source or Object
      form, that is based on (or derived from) the Work and for which the
      editorial revisions, annotations, elaborations, or other modifications
      represent, as a whole, an original work of authorship. For the purposes
      of this License, Derivative Works shall not include works that remain
      separable from, or merely link (or bind by name) to the interfaces of,
      the Work and Derivative Works thereof.

      "Contribution" shall mean any work of authorship, including
      the original version of the Work and any modifications or additions
      to that Work or Derivative Works thereof, that is intentionally
      submitted to Licensor for inclusion in the Work by the copyright owner
      or by an individual or Legal Entity authorized to submit on behalf of
      the copyright owner. For the purposes of this definition, "submitted"
      means any form of electronic, verbal, or written communication sent
      to the Licensor or its representatives, including but not limited to
      communication on electronic mailing lists, source code control systems,
      and issue tracking systems that are managed by, or on behalf of, the
      Licensor for the purpose of discussing and improving the Work, but
      excluding communication that is conspicuously marked or otherwise
      designated in writing by the copyright owner as "Not a Contribution."

      "Contributor" shall mean Licensor and any individual or Legal Entity
      on behalf of whom a Contribution has been received by Licensor and
      subsequently incorporated within the Work.

   2. Grant of Copyright License. Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      copyright license to reproduce, prepare Derivative Works of,
      publicly display, publicly perform, sublicense, and distribute the
      Work and such Derivative Works in Source or Object form.

   3. Grant of Patent License. Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      (except as stated in this section) patent license to make, have made,
      use, offer to sell, sell, import, and otherwise transfer the Work,
      where such license applies only to those patent claims licensable
      by such Contributor that are necessarily infringed by their
      Contribution(s) alone or by combination of their Contribution(s)
      with the Work to which such Contribution(s) was submitted. If You
      institute patent litigation against any entity (including a
      cross-claim or counterclaim in a lawsuit) alleging that the Work
      or a Contribution incorporated within the Work constitutes direct
      or contributory patent infringement, then any patent licenses
      granted to You under this License for that Work shall terminate
      as of the date such litigation is filed.

   4. Redistribution. You may reproduce and distribute copies of the
      Work or Derivative Works thereof in any medium, with or without
      modifications, and in Source or Object form, provided that You
      meet the following conditions:

      (a) You must give any other recipients of the Work or
          Derivative Works a copy of this License; and

      (b) You must cause any modified files to carry prominent notices
          stating that You changed the files; and

      (c) You must retain, in the Source form of any Derivative Works
          that You distribute, all copyright, patent, trademark, and
          attribution notices from the Source form of the Work,
          excluding those notices that do not pertain to any part of
          the Derivative Works; and

      (d) If the Work includes a "NOTICE" text file as part of its
          distribution, then any Derivative Works that You distribute must
          include a readable copy of the attribution notices contained
          within such NOTICE file, excluding those notices that do not
          pertain to any part of the Derivative Works, in at least one
          of the following places: within a NOTICE text file distributed
          as part of the Derivative Works; within the Source form or
          documentation, if provided along with the Derivative Works; or,
          within a display generated by the Derivative Works, if and
          wherever such third-party notices normally appear. The contents
          of the NOTICE file are for informational purposes only and
          do not modify the License. You may add Your own attribution
          notices within Derivative Works that You distribute, alongside
          or as an addendum to the NOTICE text from the Work, provided
          that such additional attribution notices cannot be construed
          as modifying the License.

      You may add Your own copyright statement to Your modifications and
      may provide additional or different license terms and conditions
      for use, reproduction, or distribution of Your modifications, or
      for any such Derivative Works as a whole, provided Your use,
      reproduction, and distribution of the Work otherwise complies with
      the conditions stated in this License.

   5. Submission of Contributions. Unless You explicitly state otherwise,
      any Contribution intentionally submitted for inclusion in the Work
      by You to the Licensor shall be under the terms and conditions of
      this License, without any additional terms or conditions.
      Notwithstanding the above, nothing herein shall supersede or modify
      the terms of any separate license agreement you may have executed
      with Licensor regarding such Contributions.

   6. Trademarks. This License does not grant permission to use the trade
      names, trademarks, service marks, or product names of the Licensor,
      except as required for reasonable and customary use in describing the
      origin of the Work and reproducing the content of the NOTICE file.

   7. Disclaimer of Warranty. Unless required by applicable law or
      agreed to in writing, Licensor provides the Work (and each
      Contributor provides its Contributions) on an "AS IS" BASIS,
      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
      implied, including, without limitation, any warranties or conditions
      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A
      PARTICULAR PURPOSE. You are solely responsible for determining the
      appropriateness of using or redistributing the Work and assume any
      risks associated with Your exercise of permissions under this License.

   8. Limitation of Liability. In no event and under no legal theory,
      whether in tort (including negligence), contract, or otherwise,
      unless required by applicable law (such as deliberate and grossly
      negligent acts) or agreed to in writing, shall any Contributor be
      liable to You for damages, including any direct, indirect, special,
      incidental, or consequential damages of any character arising as a
      result of this License or out of the use or inability to use the
      Work (including but not limited to damages for loss of goodwill,
      work stoppage, computer failure or malfunction, or any and all
      other commercial damages or losses), even if such Contributor
      has been advised of the possibility of such damages.

   9. Accepting Warranty or Additional Liability. While redistributing
      the Work or Derivative Works thereof, You may choose to offer,
      and charge a fee for, acceptance of support, warranty, indemnity,
      or other liability obligations and/or rights consistent with this
      License. However, in accepting such obligations, You may act only
      on Your own behalf and on Your sole responsibility, not on behalf
      of any other Contributor, and only if You agree to indemnify,
      defend, and hold each Contributor harmless for any liability
      incurred by, or claims asserted against, such Contributor by reason
      of your accepting any such warranty or additional liability.

   END OF TERMS AND CONDITIONS

   APPENDIX: How to apply the Apache License to your work.

      To apply the Apache License to your work, attach the following
      boilerplate notice, with the fields enclosed by brackets "[]"
      replaced with your own identifying information. (Don't include
      the brackets!)  The text should be enclosed in the appropriate
      comment syntax for the file format. We also recommend that a
      file or class name and description of purpose be included on the
      same "printed page" as the copyright notice for easier
      identification within third-party archives.

   Copyright 2018 devgianlu

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/


import android.annotation.SuppressLint;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Aria2 {
    public class BadEnvironmentException extends Exception {
        public BadEnvironmentException(String message) {
            super(message);
        }

        public BadEnvironmentException(@NonNull Throwable ex) {
            super(ex);
        }
    }

    private static final Pattern INFO_MESSAGE_PATTERN = Pattern.compile("^\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2} \\[.+] (.+)$");
    private static final String TAG = Aria2.class.getSimpleName();
    private static Aria2 instance;
    private final MessageHandler messageHandler;
    private final Object processLock = new Object();
    private Env env;
    private Monitor monitor;
    private StreamWatcher errorWatcher;
    private StreamWatcher inputWatcher;
    private Process currentProcess;

    private Aria2() {
        messageHandler = new MessageHandler();
        new Thread(messageHandler).start();
    }

    @NonNull
    public static Aria2 get() {
        if (instance == null) instance = new Aria2();
        return instance;
    }

    @NonNull
    private static String startCommandForLog(@NonNull String exec, String... params) {
        StringBuilder builder = new StringBuilder(exec);
        for (String param : params) builder.append(' ').append(param);
        return builder.toString();
    }

    private static boolean waitFor(@NonNull Process process, int timeout, @NonNull TimeUnit unit) throws InterruptedException {
        long startTime = System.nanoTime();
        long rem = unit.toNanos(timeout);

        do {
            try {
                process.exitValue();
                return true;
            } catch (IllegalThreadStateException ex) {
                if (rem > 0)
                    Thread.sleep(Math.min(TimeUnit.NANOSECONDS.toMillis(rem) + 1, 100));
            }

            rem = unit.toNanos(timeout) - (System.nanoTime() - startTime);
        } while (rem > 0);
        return false;
    }

    @Nullable
    private static String getprop(@NonNull String key) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("getprop " + key);
            try (BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String l = in.readLine();
                if (l.isEmpty()) return null;
                else return l;
            }
        } catch (IOException ex) {
            return null;
        } finally {
            if (p != null) p.destroy();
        }
    }

    @Nullable
    private File storeAllCertificates(@NonNull File parent) {
        File certs = new File(parent, "ca-certs");
        try (FileOutputStream out = new FileOutputStream(certs, false)) {
            KeyStore ks = KeyStore.getInstance("AndroidCAStore");
            if (ks != null) {
                ks.load(null, null);
                Enumeration<String> aliases = ks.aliases();

                while (aliases.hasMoreElements()) {
                    String alias = aliases.nextElement();

                    Certificate cert = ks.getCertificate(alias);
                    out.write("-----BEGIN CERTIFICATE-----\n".getBytes());
                    out.write(Base64.encode(cert.getEncoded(), 0));
                    out.write("-----END CERTIFICATE-----\n".getBytes());
                }
            }

            return certs;
        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException ex) {
            Log.e(TAG, "Failed getting CA certificates.", ex);
        }

        return null;
    }

    void addListener(@NonNull MessageListener listener) {
        messageHandler.listeners.add(listener);
    }

    void removeListener(@NonNull MessageListener listener) {
        messageHandler.listeners.remove(listener);
    }

    public boolean hasEnv() {
        return env != null && env.exec.exists();
    }

    @NonNull
    public String version() throws BadEnvironmentException, IOException {
        if (env == null)
            throw new BadEnvironmentException("Missing environment!");

        try {
            Process process = execWithParams(false, "-v");
            process.waitFor();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String str = reader.readLine();
                return str == null ? "" : str;
            }
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        }
    }

    @NonNull
    private Process execWithParams(boolean redirect, String... params) throws BadEnvironmentException, IOException {
        if (env == null)
            throw new BadEnvironmentException("Missing environment!");

        String[] cmdline = new String[params.length + 1];
        cmdline[0] = env.execPath();
        System.arraycopy(params, 0, cmdline, 1, params.length);
        Process process = new ProcessBuilder(cmdline).redirectErrorStream(redirect).start();
        if (process == null) throw new IOException("Process is null!");
        return process;
    }

    public void loadEnv(@NonNull File parent, @NonNull File exec, @NonNull File session, Aria2Settings settings) throws BadEnvironmentException {
        if (!exec.exists())
            throw new BadEnvironmentException(exec.getAbsolutePath() + " doesn't exists!");

        if (!exec.canExecute() && !exec.setExecutable(true))
            throw new BadEnvironmentException(exec.getAbsolutePath() + " can't be executed!");

        if (session.exists()) {
            if (!session.canRead() && !session.setReadable(true))
                throw new BadEnvironmentException(session.getAbsolutePath() + " can't be read!");
        } else {
            try {
                if (!session.createNewFile())
                    throw new BadEnvironmentException(session.getAbsolutePath() + " can't be created!");
            } catch (IOException ex) {
                throw new BadEnvironmentException(ex);
            }
        }

        this.env = new Env(parent, exec, session, storeAllCertificates(parent), settings);
    }

    boolean start() throws BadEnvironmentException, IOException {
        if (currentProcess != null) {
            postMessage(Message.obtain(Message.Type.PROCESS_STARTED, "[already started]"));
            return false;
        }

        if (env == null)
            throw new BadEnvironmentException("Missing environment!");

        reloadEnv();

        String execPath = env.execPath();
        String[] params = env.startArgs();

        synchronized (processLock) {
            currentProcess = execWithParams(true, params);
            new Thread(new Waiter(currentProcess), "aria2android-waiterThread").start();
            new Thread(this.inputWatcher = new StreamWatcher(currentProcess.getInputStream()), "aria2-android-inputWatcherThread").start();
            new Thread(this.errorWatcher = new StreamWatcher(currentProcess.getErrorStream()), "aria2-android-errorWatcherThread").start();
        }

        //if (Prefs.getBoolean(Aria2PK.SHOW_PERFORMANCE))
        //    new Thread(this.monitor = new Monitor(), "aria2android-monitorThread").start();

        postMessageDelayed(Message.obtain(Message.Type.PROCESS_STARTED, startCommandForLog(execPath, params)), 500 /* Ensure service is started */);
        return true;
    }

    private void reloadEnv() throws BadEnvironmentException {
        if (env == null)
            throw new BadEnvironmentException("Missing environment!");

        loadEnv(env.parent, env.exec, env.session, env.settings);
    }

    private void processTerminated(int code) {
        postMessage(Message.obtain(Message.Type.PROCESS_TERMINATED, code));

        if (monitor != null) {
            monitor.close();
            monitor = null;
        }

        if (errorWatcher != null) {
            errorWatcher.close();
            errorWatcher = null;
        }

        if (inputWatcher != null) {
            inputWatcher.close();
            inputWatcher = null;
        }

        stop();
    }

    private void monitorFailed(@NonNull Exception ex) {
        postMessage(Message.obtain(Message.Type.MONITOR_FAILED, ex));
    }

    private void postMessage(@NonNull Message message) {
        message.delay = 0;
        messageHandler.queue.add(message);
        message.log(TAG);
    }

    private void postMessageDelayed(@NonNull Message message, int millis) {
        message.delay = millis;
        messageHandler.queue.add(message);
        message.log(TAG);
    }

    private void handleStreamMessage(@NonNull String line) {
        if (line.startsWith("WARNING: ")) {
            postMessage(Message.obtain(Message.Type.PROCESS_WARN, line.substring(9)));
        } else if (line.startsWith("ERROR: ")) {
            postMessage(Message.obtain(Message.Type.PROCESS_ERROR, line.substring(7)));
        } else {
            String clean;
            Matcher matcher = INFO_MESSAGE_PATTERN.matcher(line);
            if (matcher.find()) clean = matcher.group(1);
            else clean = line;
            postMessage(Message.obtain(Message.Type.PROCESS_INFO, clean));
        }
    }

    void stop() {
        synchronized (processLock) {
            if (currentProcess != null) {
                currentProcess.destroy();
                currentProcess = null;
            }
        }
    }

    public boolean delete() {
        stop();
        return env.delete();
    }

    public boolean isRunning() {
        return currentProcess != null;
    }

    public interface MessageListener {
        void onMessage(@NonNull Message msg);
    }

    private static class MessageHandler implements Runnable, Closeable {
        private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
        private final List<MessageListener> listeners = new ArrayList<>();
        private volatile boolean shouldStop = false;

        @Override
        public void run() {
            while (!shouldStop) {
                try {
                    Message msg = queue.take();

                    if (msg.delay > 0)
                        Thread.sleep(msg.delay);

                    for (MessageListener listener : new ArrayList<>(listeners))
                        listener.onMessage(msg);

                    msg.recycle();
                } catch (InterruptedException ex) {
                    Log.w(TAG, ex);
                    close();
                }
            }
        }

        @Override
        public void close() {
            shouldStop = true;
        }
    }

    private static class Env {
        private final File parent;
        private final File exec;
        private final File session;
        private final Aria2Settings settings;
        private final Map<String, String> params;

        Env(@NonNull File parent, @NonNull File exec, @NonNull File session, @Nullable File cacerts, Aria2Settings settings) {
            this.parent = parent;
            this.exec = exec;
            this.session = session;
            this.settings = settings;
            this.params = new HashMap<>();

            // Can be overridden
            //if (Prefs.getBoolean(Aria2PK.SAVE_SESSION))
            params.put("--save-session-interval", "30");

            String dns1 = getprop("net.dns1");
            String dns2 = getprop("net.dns2");
            if (dns1 != null || dns2 != null) {
                String dnsString = dns1 != null ? dns1 : dns2;
                if (dns1 != null) dnsString += "," + dns1;
                else dnsString += "," + dns2;

                params.put("--async-dns", "true");
                params.put("--async-dns-server", dnsString);
            }

            //if (Prefs.getBoolean(Aria2PK.CHECK_CERTIFICATE) && cacerts != null) {
            //    params.put("--check-certificate", "true");
            //    params.put("--ca-certificate", cacerts.getAbsolutePath());
            //} else {
            params.put("--check-certificate", "false");
            //}

            loadCustomOptions(params);

            // Cannot be overridden
            params.put("--daemon", "false");
            params.put("--enable-color", "false");
            params.put("--enable-rpc", "true");
            params.put("--rpc-secret", settings.getToken());
            //params.put("--rpc-passwd", "helloworld");
            params.put("--rpc-listen-port", String.valueOf(settings.getPort()));
            params.put("--rpc-listen-all", "false");
            params.put("--rpc-allow-origin-all", "false");
            //params.put("--rpc-secret", Prefs.getString(Aria2PK.RPC_TOKEN));
            //params.put("--rpc-listen-port", String.valueOf(Prefs.getInt(Aria2PK.RPC_PORT, 6800)));
            //params.put("--dir", Prefs.getString(Aria2PK.OUTPUT_DIRECTORY));
            //params.put("--rpc-listen-all", Boolean.toString(Prefs.getBoolean(Aria2PK.RPC_LISTEN_ALL)));
            //params.put("--rpc-allow-origin-all", Boolean.toString(Prefs.getBoolean(Aria2PK.RPC_LISTEN_ALL)));
            //params.put("--dir", "/storage/emulated/0/Download");
            params.put("--dir", "/data/user/0/com.lagradost.fetchbutton.example/files/");

            //if (Prefs.getBoolean(Aria2PK.SAVE_SESSION)) {
            params.put("--input-file", "/data/user/0/com.lagradost.fetchbutton.example/files/session");//session.getAbsolutePath());
            params.put("--save-session", "/data/user/0/com.lagradost.fetchbutton.example/files/session");
            //}
        }

        private static void loadCustomOptions(@NonNull Map<String, String> options) {
            /*try {
                JSONObject obj = JsonStoring.intoPrefs().getJsonObject(Aria2PK.CUSTOM_OPTIONS);
                if (obj == null) return;

                Iterator<String> iterator = obj.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    options.put("--" + key, obj.getString(key));
                }
            } catch (JSONException ex) {
                Log.e(TAG, "Failed loading custom options.", ex);
            }*/
        }


        @NonNull
        String[] startArgs() {
            String[] args = new String[params.size()];
            int i = 0;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getValue() == null || entry.getValue().isEmpty())
                    args[i] = entry.getKey();
                else
                    args[i] = entry.getKey() + "=" + entry.getValue();

                i++;
            }
            return args;
        }

        @NonNull
        String execPath() {
            return exec.getAbsolutePath();
        }

        boolean delete() {
            return session.delete();
        }
    }

    private abstract static class TopParser {
        static final Pattern TOP_OLD_PATTERN = Pattern.compile("(\\d*?)\\s+(\\d*?)\\s+(\\d*?)%\\s(.)\\s+(\\d*?)\\s+(\\d*?)K\\s+(\\d*?)K\\s+(..)\\s(.*?)\\s+(.*)$");
        static final Pattern TOP_NEW_PATTERN = Pattern.compile("(\\d+)\\s+(\\d+\\.\\d+)\\s+([\\d|.]+?.)\\s+(.*)$");
        static final TopParser OLD_PARSER = new TopParser(TOP_OLD_PATTERN, 1, 3, 7) {
            @Override
            boolean matches(@NonNull String line) {
                return line.endsWith("aria2c.so");
            }

            @NonNull
            @Override
            String getCommand(int delaySec) {
                return "top -d " + delaySec;
            }

            @Override
            int getMemoryBytes(@NonNull String match) {
                return Integer.parseInt(match) * 1024;
            }
        };
        static final TopParser NEW_PARSER = new TopParser(TOP_NEW_PATTERN, 1, 2, 3) {
            @Override
            int getMemoryBytes(@NonNull String match) {
                int multiplier;
                char lastChar = match.charAt(match.length() - 1);
                if (Character.isAlphabetic(lastChar)) {
                    switch (lastChar) {
                        case 'K':
                            multiplier = 1024;
                            break;
                        case 'M':
                            multiplier = 1024 * 1024;
                            break;
                        case 'G':
                            multiplier = 1024 * 1024 * 1024;
                            break;
                        default:
                            multiplier = 1;
                            break;
                    }
                } else {
                    multiplier = 1;
                }

                return (int) (Float.parseFloat(match.substring(0, match.length() - 1)) * multiplier);
            }

            @Override
            boolean matches(@NonNull String line) {
                return line.contains("aria2c");
            }

            @SuppressLint("DefaultLocale")
            @NonNull
            @Override
            String getCommand(int delaySec) {
                return String.format("top -d %d -q -b -o PID,%%CPU,RES,CMDLINE", delaySec);
            }
        };
        private final Pattern pattern;
        private final int[] pidCpuRss;

        TopParser(@NonNull Pattern pattern, int... pidCpuRss) {
            this.pattern = pattern;
            this.pidCpuRss = pidCpuRss;
            if (pidCpuRss.length != 3) throw new IllegalArgumentException();
        }

        @Nullable
        final MonitorUpdate parseLine(@NonNull String line) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                try {
                    return MonitorUpdate.obtain(Integer.parseInt(matcher.group(pidCpuRss[0])), matcher.group(pidCpuRss[1]), getMemoryBytes(matcher.group(pidCpuRss[2])));
                } catch (Exception ex) {
                    Log.e(TAG, "Failed parsing `top` line: " + line, ex);
                }
            }

            return null;
        }

        abstract int getMemoryBytes(@NonNull String match);

        abstract boolean matches(@NonNull String line);

        @NonNull
        abstract String getCommand(int delaySec);
    }

    private class StreamWatcher implements Runnable, Closeable {
        private final InputStream stream;
        private volatile boolean shouldStop = false;

        StreamWatcher(@NonNull InputStream stream) {
            this.stream = stream;
        }

        @Override
        public void run() {
            try (Scanner scanner = new Scanner(stream)) {
                while (!shouldStop && scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (!line.isEmpty()) handleStreamMessage(line);
                }
            }
        }

        @Override
        public void close() {
            shouldStop = true;
        }
    }

    private class Monitor implements Runnable, Closeable {
        private final byte[] INVALID_STRING = "Invalid argument".getBytes();
        private volatile boolean shouldStop = false;

        @Nullable
        private TopParser selectPattern() throws IOException, InterruptedException {
            Process process = Runtime.getRuntime().exec("top --version");

            if (waitFor(process, 1000, TimeUnit.MILLISECONDS)) {
                int exitCode = process.exitValue();
                if (exitCode != 0) {
                    byte[] buffer = new byte[INVALID_STRING.length];
                    if (buffer.length != process.getErrorStream().read(buffer) || !Arrays.equals(buffer, INVALID_STRING)) {
                        Log.e(TAG, String.format(Locale.getDefault(), "Couldn't identify `top` version. {invalidString: %s, exitCode: %d}", new String(buffer), exitCode));
                        return null;
                    } else {
                        return TopParser.OLD_PARSER;
                    }
                } else {
                    return TopParser.NEW_PARSER;
                }
            } else {
                Log.e(TAG, "Couldn't identify `top` version, process didn't exit within 1000ms.");
                return null;
            }
        }

        @Override
        public void run() {
            TopParser parser;
            try {
                parser = selectPattern();
                if (parser == null) {
                    postMessage(Message.obtain(Message.Type.MONITOR_FAILED));
                    return;
                }
            } catch (IOException | InterruptedException ex) {
                Log.e(TAG, "Couldn't find suitable pattern for `top`.", ex);
                return;
            }

            Process process = null;
            try {
                process = Runtime.getRuntime().exec(parser.getCommand(1));//Prefs.getInt(Aria2PK.NOTIFICATION_UPDATE_DELAY, 1)));
                try (Scanner scanner = new Scanner(process.getInputStream())) {
                    while (!shouldStop && scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (parser.matches(line)) {
                            MonitorUpdate update = parser.parseLine(line);
                            if (update != null)
                                postMessage(Message.obtain(Message.Type.MONITOR_UPDATE, update));
                        }
                    }
                }
            } catch (IOException ex) {
                monitorFailed(ex);
            } finally {
                if (process != null) process.destroy();
            }
        }

        @Override
        public void close() {
            shouldStop = true;
        }
    }

    private class Waiter implements Runnable {
        private final Process process;

        Waiter(@NonNull Process process) {
            this.process = process;
        }

        @Override
        public void run() {
            try {
                int exit = process.waitFor();
                processTerminated(exit);
            } catch (InterruptedException ex) {
                processTerminated(999);
                Log.w(TAG, ex);
            }
        }
    }
}
