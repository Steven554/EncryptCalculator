package com.example.encrypit

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main2.*
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class MainActivity2 : AppCompatActivity() {
    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val context = applicationContext
        val path = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

        // Create new plaintext file
        val fileName = "data.txt"
        var testfile = File(path, fileName)

        if (testfile.createNewFile()) {
            println("File created")
        } else {
            println("File already exists")
            textViewField.text = testfile.readText()
        }

        // Create new calc password file
        val calcCodeFile = File(path, "holder")

        if (calcCodeFile.createNewFile()) {
            println("File created")
            calcCodeFile.writeText("911")
        } else {
            println("File already exists")
        }

        // Cipher IV
        lateinit var iv : ByteArray

        lateinit var pwdCalcHolder : String

        // Key alias holder
        lateinit var aliasHolder : String

        // Text view field which contents are encrypted
        textViewField.setOnClickListener() {
            try {
                textViewField.setCursorVisible(true)
                textViewField.setFocusableInTouchMode(true)
                textViewField.setInputType(InputType.TYPE_CLASS_TEXT)
                textViewField.requestFocus()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Pwd field button as an AlertDialogue
        val aliasBut = findViewById<View>(R.id.passwordField) as Button
        aliasBut.setOnClickListener {
            try {
                val alertDialog = AlertDialog.Builder(this).create()
//                alertDialog.setMessage("")
                var pwdInputField = EditText(this)
                alertDialog.setView(pwdInputField)

                // OK Button in AlertDialogue
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Enter Password") { dialog, which ->
                    // Storing password for comparison in other buttons
                    aliasHolder = pwdInputField.text.toString()
                }

                alertDialog.show()

                // Sets OK button centered
                val btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
                layoutParams.weight = 10f

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        //  Calc pwd button as an AlertDialogue
        val calcPwd = findViewById<View>(R.id.calcPwd) as Button
        calcPwd.setOnClickListener {
            try {
                val alertDialog = AlertDialog.Builder(this).create()
//                alertDialog.setMessage("")
                var pwdCalcField = EditText(this)
                alertDialog.setView(pwdCalcField)

                // OK Button in AlertDialogue
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK") { dialog, which ->
                    // Storing password for comparison in other buttons
                    pwdCalcHolder  = pwdCalcField.text.toString()
                }

                alertDialog.show()

                // Sets OK button centered
                val btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
                layoutParams.weight = 10f

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Encrypt Button
        val encryptBut = findViewById<View>(R.id.Encrypt) as Button
        encryptBut.setOnClickListener {
            try {
                // Instance of Androids KeyGenerator
                val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")

                // build KeyGenParameterSpec with parameters of key, storing in keystore using keystore alias
                val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                    // aliasHolder is user defined key alias
                    aliasHolder, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()

                keyGenerator.init(keyGenParameterSpec)
                val secretKey = keyGenerator.generateKey()

                val cipher = Cipher.getInstance("AES/GCM/NoPadding")
                cipher.init(Cipher.ENCRYPT_MODE, secretKey)

                // Passing IV to outer scope so it can by used by decryption
                iv = cipher.iv

                var holder = textViewField.text.toString()

                val encryption = cipher.doFinal(holder.toByteArray()) // default UTF-8

                testfile.writeBytes(encryption)

                textViewField.text = testfile.readText()

            } catch (e: Exception) {
                e.printStackTrace()
                // Exception prints failure
                Toast.makeText(this@MainActivity2, "Encryption Failure!", Toast.LENGTH_LONG)
                    .show()
            }
        }

        // Decrypt button
        val decBut = findViewById<View>(R.id.Decrypt) as Button
        decBut.setOnClickListener {
                try {
                    // Creating keystore instance
                    val keyStore = KeyStore.getInstance("AndroidKeyStore")
                    keyStore.load(null)

                    // Gets our secret key from keystore with alias
                    val secretKeyEntry = keyStore
                        .getEntry(aliasHolder, null) as KeyStore.SecretKeyEntry

                    val decrsecretKey: SecretKey = secretKeyEntry.secretKey
                    val cipher =
                        Cipher.getInstance("AES/GCM/NoPadding")
                    // Passing encryption IV to decryption cipher
                    val spec = GCMParameterSpec(128, iv)
                    cipher.init(Cipher.DECRYPT_MODE, decrsecretKey, spec)

                    val encryption = cipher.doFinal(testfile.readBytes())

                    testfile.writeBytes(encryption)

                    textViewField.text = testfile.readText()

                } catch (e: Exception) {
                    e.printStackTrace()
                    // Exception prints failure
                    Toast.makeText(this@MainActivity2, "Decryption Failed!", Toast.LENGTH_LONG)
                        .show()
                }
        }
    }
}

