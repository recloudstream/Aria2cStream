package com.lagradost.fetchbutton.aria2c

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import java.io.*
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

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
*/   class Aria2 private constructor() {
    inner class BadEnvironmentException : Exception {
        constructor(message: String?) : super(message)
        constructor(ex: Throwable) : super(ex)
    }

    private val messageHandler: MessageHandler
    private val processLock = Any()
    private var env: Env? = null
    private var monitor: Monitor? = null
    private var errorWatcher: StreamWatcher? = null
    private var inputWatcher: StreamWatcher? = null
    private var currentProcess: Process? = null
    private fun storeAllCertificates(parent: File): File? {
        val certs = File(parent, "ca-certs")
        try {
            FileOutputStream(certs, false).use { out ->
                val ks =
                    KeyStore.getInstance("AndroidCAStore")
                if (ks != null) {
                    ks.load(null, null)
                    val aliases = ks.aliases()
                    while (aliases.hasMoreElements()) {
                        val alias = aliases.nextElement()
                        val cert = ks.getCertificate(alias)
                        out.write("-----BEGIN CERTIFICATE-----\n".toByteArray())
                        out.write(Base64.encode(cert.encoded, 0))
                        out.write("-----END CERTIFICATE-----\n".toByteArray())
                    }
                }
                return certs
            }
        } catch (ex: IOException) {
            Log.e(TAG, "Failed getting CA certificates.", ex)
        } catch (ex: KeyStoreException) {
            Log.e(TAG, "Failed getting CA certificates.", ex)
        } catch (ex: CertificateException) {
            Log.e(TAG, "Failed getting CA certificates.", ex)
        } catch (ex: NoSuchAlgorithmException) {
            Log.e(TAG, "Failed getting CA certificates.", ex)
        }
        return null
    }

    fun addListener(listener: MessageListener) {
        messageHandler.listeners.add(listener)
    }

    fun removeListener(listener: MessageListener) {
        messageHandler.listeners.remove(listener)
    }

    fun hasEnv(): Boolean {
        return env != null && env!!.exec.exists()
    }

    @Throws(BadEnvironmentException::class, IOException::class)
    fun version(): String {
        if (env == null) throw BadEnvironmentException("Missing environment!")
        try {
            val process = execWithParams(false, listOf("-v"))
            process.waitFor()
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                val str = reader.readLine()
                return str ?: ""
            }
        } catch (ex: InterruptedException) {
            throw IOException(ex)
        }
    }

    @Throws(
        BadEnvironmentException::class,
        IOException::class
    )
    private fun execWithParams(
        redirect: Boolean,
        params: List<String>
    ): Process {
        if (env == null) throw BadEnvironmentException("Missing environment!")
        val copy = ArrayList<String>(params.size)
        copy.add(env!!.execPath())
        copy.addAll(params)
        return ProcessBuilder(copy).redirectErrorStream(redirect).start()
            ?: throw IOException("Process is null!")
    }

    @Throws(BadEnvironmentException::class)
    fun loadEnv(parent: File, exec: File, session: File, settings: Aria2Settings) {
        if (!exec.exists()) throw BadEnvironmentException(exec.absolutePath + " doesn't exists!")
        if (!exec.canExecute() && !exec.setExecutable(true)) throw BadEnvironmentException(exec.absolutePath + " can't be executed!")
        if (session.exists()) {
            if (!session.canRead() && !session.setReadable(true)) throw BadEnvironmentException(
                session.absolutePath + " can't be read!"
            )
        } else {
            try {
                if (!session.createNewFile()) throw BadEnvironmentException(session.absolutePath + " can't be created!")
            } catch (ex: IOException) {
                throw BadEnvironmentException(ex)
            }
        }
        env = Env(parent, exec, session, storeAllCertificates(parent), settings)
    }

    @Throws(BadEnvironmentException::class, IOException::class)
    fun start(): Boolean {
        if (currentProcess != null) {
            postMessage(Message.obtain(Message.Type.PROCESS_STARTED, "[already started]"))
            return false
        }
        if (env == null) throw BadEnvironmentException("Missing environment!")
        reloadEnv()
        val execPath = env!!.execPath()
        val params = env!!.startArgs().filterNotNull()
        synchronized(processLock) {
            currentProcess = execWithParams(true, params)
            Thread(
                Waiter(currentProcess!!),
                "aria2android-waiterThread"
            ).start()
            Thread(
                StreamWatcher(currentProcess!!.inputStream)
                    .also {
                        inputWatcher = it
                    }, "aria2-android-inputWatcherThread"
            ).start()
            Thread(
                StreamWatcher(currentProcess!!.errorStream)
                    .also {
                        errorWatcher = it
                    }, "aria2-android-errorWatcherThread"
            ).start()
        }

        //if (Prefs.getBoolean(Aria2PK.SHOW_PERFORMANCE))
        //    new Thread(this.monitor = new Monitor(), "aria2android-monitorThread").start();
        postMessageDelayed(
            Message.obtain(
                Message.Type.PROCESS_STARTED,
                startCommandForLog(execPath, params)
            ), 500 /* Ensure service is started */
        )
        return true
    }

    @Throws(BadEnvironmentException::class)
    private fun reloadEnv() {
        if (env == null) throw BadEnvironmentException("Missing environment!")
        loadEnv(env!!.parent, env!!.exec, env!!.session, env!!.settings)
    }

    private fun processTerminated(code: Int) {
        postMessage(Message.obtain(Message.Type.PROCESS_TERMINATED, code))
        if (monitor != null) {
            monitor!!.close()
            monitor = null
        }
        if (errorWatcher != null) {
            errorWatcher!!.close()
            errorWatcher = null
        }
        if (inputWatcher != null) {
            inputWatcher!!.close()
            inputWatcher = null
        }
        stop()
    }

    private fun monitorFailed(ex: Exception) {
        postMessage(Message.obtain(Message.Type.MONITOR_FAILED, ex))
    }

    private fun postMessage(message: Message) {
        message.delay = 0
        messageHandler.queue.add(message)
        message.log(TAG)
    }

    private fun postMessageDelayed(message: Message, millis: Int) {
        message.delay = millis
        messageHandler.queue.add(message)
        message.log(TAG)
    }

    private fun handleStreamMessage(line: String) {
        if (line.startsWith("WARNING: ")) {
            postMessage(Message.obtain(Message.Type.PROCESS_WARN, line.substring(9)))
        } else if (line.startsWith("ERROR: ")) {
            postMessage(Message.obtain(Message.Type.PROCESS_ERROR, line.substring(7)))
        } else {
            val clean: String
            val matcher = INFO_MESSAGE_PATTERN.matcher(line)
            clean = if (matcher.find()) matcher.group(1) else line
            postMessage(Message.obtain(Message.Type.PROCESS_INFO, clean))
        }
    }

    private fun stop() {
        synchronized(processLock) {
            if (currentProcess != null) {
                currentProcess!!.destroy()
                currentProcess = null
            }
        }
    }

    fun delete(): Boolean {
        stop()
        return env!!.delete()
    }

    val isRunning: Boolean
        get() = currentProcess != null

    interface MessageListener {
        fun onMessage(msg: Message)
    }

    private class MessageHandler : Runnable, Closeable {
        val queue: BlockingQueue<Message> = LinkedBlockingQueue()
        val listeners: MutableList<MessageListener> = ArrayList()

        @Volatile
        private var shouldStop = false
        override fun run() {
            while (!shouldStop) {
                try {
                    val msg = queue.take()
                    if (msg.delay > 0) Thread.sleep(msg.delay.toLong())
                    for (listener in ArrayList(listeners)) listener.onMessage(msg)
                    msg.recycle()
                } catch (ex: InterruptedException) {
                    Log.w(TAG, ex)
                    close()
                }
            }
        }

        override fun close() {
            shouldStop = true
        }
    }

    private class Env(
        val parent: File,
        val exec: File,
        val session: File,
        val cert: File?,
        val settings: Aria2Settings
    ) {
        private val params: MutableMap<String, String?>
        fun startArgs(): Array<String?> {
            val args = arrayOfNulls<String>(params.size)
            var i = 0
            for ((key, value) in params) {
                if (value == null || value.isEmpty()) args[i] =
                    key else args[i] = "$key=$value"
                i++
            }
            return args
        }

        fun execPath(): String {
            return exec.absolutePath
        }

        fun delete(): Boolean {
            return session.delete()
        }


        init {
            params = HashMap()

            params["--save-session-interval"] = "30"
            val dns1 = getprop("net.dns1")
            val dns2 = getprop("net.dns2")
            if (dns1 != null || dns2 != null) {
                var dnsString = dns1 ?: dns2
                dnsString += if (dns1 != null) ",$dns1" else ",$dns2"
                params["--async-dns"] = "true"
                params["--async-dns-server"] = dnsString
            }

            //if (Prefs.getBoolean(Aria2PK.CHECK_CERTIFICATE) && cacerts != null) {
            //    params.put("--check-certificate", "true");
            //    params.put("--ca-certificate", cacerts.getAbsolutePath());
            //} else {
            params["--check-certificate"] = "false"//settings.checkCertificate.toString()
            //}

            // Cannot be overridden
            params["--daemon"] = "false"
            params["--enable-color"] = "false"
            params["--enable-rpc"] = "true"
            params["--rpc-secret"] = settings.token
            params["--rpc-listen-port"] = settings.availablePort.toString()
            params["--rpc-listen-all"] = "false"
            params["--rpc-allow-origin-all"] = "false"
            //params.put("--rpc-secret", Prefs.getString(Aria2PK.RPC_TOKEN));
            //params.put("--rpc-listen-port", String.valueOf(Prefs.getInt(Aria2PK.RPC_PORT, 6800)));
            //params.put("--dir", Prefs.getString(Aria2PK.OUTPUT_DIRECTORY));
            //params.put("--rpc-listen-all", Boolean.toString(Prefs.getBoolean(Aria2PK.RPC_LISTEN_ALL)));
            //params.put("--rpc-allow-origin-all", Boolean.toString(Prefs.getBoolean(Aria2PK.RPC_LISTEN_ALL)));
            //params.put("--dir", "/storage/emulated/0/Download");
            params["--dir"] = settings.dir

            if (settings.sessionDir != null) {
                params["--input-file"] =
                    settings.sessionDir
                params["--save-session"] =
                    settings.sessionDir
            }
        }
    }

    private abstract class TopParser(
        private val pattern: Pattern,
        private vararg val pidCpuRss: Int
    ) {
        fun parseLine(line: String): MonitorUpdate? {
            val matcher = pattern.matcher(line)
            if (matcher.find()) {
                try {
                    return matcher.group(pidCpuRss[0])?.let {
                        matcher.group(
                            pidCpuRss[1]
                        )?.let { it1 ->
                            MonitorUpdate.obtain(
                                it.toInt(), it1, getMemoryBytes(matcher.group(pidCpuRss[2]))
                            )
                        }
                    }
                } catch (ex: Exception) {
                    Log.e(
                        TAG,
                        "Failed parsing `top` line: $line", ex
                    )
                }
            }
            return null
        }

        abstract fun getMemoryBytes(match: String): Int
        abstract fun matches(line: String): Boolean
        abstract fun getCommand(delaySec: Int): String

        companion object {
            val TOP_OLD_PATTERN =
                Pattern.compile("(\\d*?)\\s+(\\d*?)\\s+(\\d*?)%\\s(.)\\s+(\\d*?)\\s+(\\d*?)K\\s+(\\d*?)K\\s+(..)\\s(.*?)\\s+(.*)$")
            val TOP_NEW_PATTERN =
                Pattern.compile("(\\d+)\\s+(\\d+\\.\\d+)\\s+([\\d|.]+?.)\\s+(.*)$")
            val OLD_PARSER: TopParser = object : TopParser(TOP_OLD_PATTERN, 1, 3, 7) {
                override fun matches(line: String): Boolean {
                    return line.endsWith("aria2c.so")
                }

                override fun getCommand(delaySec: Int): String {
                    return "top -d $delaySec"
                }

                override fun getMemoryBytes(match: String): Int {
                    return match.toInt() * 1024
                }
            }
            val NEW_PARSER: TopParser = object : TopParser(TOP_NEW_PATTERN, 1, 2, 3) {
                override fun getMemoryBytes(match: String): Int {
                    val multiplier: Int
                    val lastChar = match[match.length - 1]
                    multiplier = if (Character.isAlphabetic(lastChar.code)) {
                        when (lastChar) {
                            'K' -> 1024
                            'M' -> 1024 * 1024
                            'G' -> 1024 * 1024 * 1024
                            else -> 1
                        }
                    } else {
                        1
                    }
                    return (match.substring(0, match.length - 1).toFloat() * multiplier).toInt()
                }

                override fun matches(line: String): Boolean {
                    return line.contains("aria2c")
                }

                @SuppressLint("DefaultLocale")
                override fun getCommand(delaySec: Int): String {
                    return String.format("top -d %d -q -b -o PID,%%CPU,RES,CMDLINE", delaySec)
                }
            }
        }

        init {
            require(pidCpuRss.size == 3)
        }
    }

    private inner class StreamWatcher(private val stream: InputStream) :
        Runnable, Closeable {
        @Volatile
        private var shouldStop = false
        override fun run() {
            Scanner(stream).use { scanner ->
                while (!shouldStop && scanner.hasNextLine()) {
                    val line = scanner.nextLine()
                    if (!line.isEmpty()) handleStreamMessage(line)
                }
            }
        }

        override fun close() {
            shouldStop = true
        }
    }

    private inner class Monitor : Runnable, Closeable {
        private val INVALID_STRING = "Invalid argument".toByteArray()

        @Volatile
        private var shouldStop = false

        @Throws(IOException::class, InterruptedException::class)
        private fun selectPattern(): TopParser? {
            val process = Runtime.getRuntime().exec("top --version")
            return if (waitFor(process, 1000, TimeUnit.MILLISECONDS)) {
                val exitCode = process.exitValue()
                if (exitCode != 0) {
                    val buffer = ByteArray(INVALID_STRING.size)
                    if (buffer.size != process.errorStream.read(buffer) || !Arrays.equals(
                            buffer,
                            INVALID_STRING
                        )
                    ) {
                        Log.e(
                            TAG,
                            String.format(
                                Locale.getDefault(),
                                "Couldn't identify `top` version. {invalidString: %s, exitCode: %d}",
                                String(buffer),
                                exitCode
                            )
                        )
                        null
                    } else {
                        TopParser.OLD_PARSER
                    }
                } else {
                    TopParser.NEW_PARSER
                }
            } else {
                Log.e(TAG, "Couldn't identify `top` version, process didn't exit within 1000ms.")
                null
            }
        }

        override fun run() {
            val parser: TopParser?
            try {
                parser = selectPattern()
                if (parser == null) {
                    postMessage(Message.obtain(Message.Type.MONITOR_FAILED))
                    return
                }
            } catch (ex: IOException) {
                Log.e(TAG, "Couldn't find suitable pattern for `top`.", ex)
                return
            } catch (ex: InterruptedException) {
                Log.e(TAG, "Couldn't find suitable pattern for `top`.", ex)
                return
            }
            var process: Process? = null
            try {
                process = Runtime.getRuntime()
                    .exec(parser.getCommand(1)) //Prefs.getInt(Aria2PK.NOTIFICATION_UPDATE_DELAY, 1)));
                Scanner(process.inputStream).use { scanner ->
                    while (!shouldStop && scanner.hasNextLine()) {
                        val line = scanner.nextLine()
                        if (parser.matches(line)) {
                            val update = parser.parseLine(line)
                            if (update != null) postMessage(
                                Message.obtain(
                                    Message.Type.MONITOR_UPDATE,
                                    update
                                )
                            )
                        }
                    }
                }
            } catch (ex: IOException) {
                monitorFailed(ex)
            } finally {
                process?.destroy()
            }
        }

        override fun close() {
            shouldStop = true
        }
    }

    private inner class Waiter(private val process: Process) : Runnable {
        override fun run() {
            try {
                val exit = process.waitFor()
                processTerminated(exit)
            } catch (ex: InterruptedException) {
                processTerminated(999)
                Log.w(TAG, ex)
            }
        }
    }

    companion object {
        private val INFO_MESSAGE_PATTERN =
            Pattern.compile("^\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2} \\[.+] (.+)$")
        private val TAG = Aria2::class.java.simpleName
        private var instance: Aria2? = null
        fun get(): Aria2 {
            if (instance == null) instance = Aria2()
            return instance!!
        }

        private fun startCommandForLog(exec: String, params: List<String>): String {
            val builder = StringBuilder(exec)
            for (param in params) builder.append(' ').append(param)
            return builder.toString()
        }

        @Throws(InterruptedException::class)
        private fun waitFor(process: Process, timeout: Int, unit: TimeUnit): Boolean {
            val startTime = System.nanoTime()
            var rem = unit.toNanos(timeout.toLong())
            do {
                try {
                    process.exitValue()
                    return true
                } catch (ex: IllegalThreadStateException) {
                    if (rem > 0) Thread.sleep(Math.min(TimeUnit.NANOSECONDS.toMillis(rem) + 1, 100))
                }
                rem = unit.toNanos(timeout.toLong()) - (System.nanoTime() - startTime)
            } while (rem > 0)
            return false
        }

        private fun getprop(key: String): String? {
            var p: Process? = null
            try {
                p = Runtime.getRuntime().exec("getprop $key")
                BufferedReader(InputStreamReader(p.inputStream)).use { `in` ->
                    val l = `in`.readLine()
                    return if (l.isEmpty()) null else l
                }
            } catch (ex: IOException) {
                return null
            } finally {
                p?.destroy()
            }
        }
    }

    init {
        messageHandler = MessageHandler()
        Thread(messageHandler).start()
    }
}