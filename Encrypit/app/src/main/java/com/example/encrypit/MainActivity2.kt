package com.example.encrypit

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
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
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec


class MainActivity2 : AppCompatActivity() {
    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val context = applicationContext
        val path = context.filesDir

        // Create new file
        val fileName = "data.txt"
        var testfile = File(path, fileName)
        testfile.createNewFile()

        // Create new file
        val encFileName = "encryptedFile.txt"
        var encFile = File(path, encFileName)
        encFile.createNewFile()

        // Key and Cipher generation
        val keygen = KeyGenerator.getInstance("AES")
        keygen.init(256)
        val key: SecretKey = keygen.generateKey()
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        var cipherText = "holder".toByteArray()
        // setting IV
        val ivBytes = byteArrayOf(
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00
        )
        val ivParameterSpec = IvParameterSpec(ivBytes)

        // Text to be encrypted
        var currText: String

        // Password
        val requiredPwd = "Encrypit"
        var pwdHolder = ""

        // Text view field which contents are encrypted
        textViewField.setOnClickListener() {
            if (pwdHolder == requiredPwd) {
                try {
                    if (textViewField.text == null) {
                        textViewField.text = "Secret text"
                    }
                    textViewField.setCursorVisible(true);
                    textViewField.setFocusableInTouchMode(true);
                    textViewField.setInputType(InputType.TYPE_CLASS_TEXT);
                    textViewField.requestFocus(); //to trigger the soft input
                    currText = textViewField.text.toString()

                } catch (e: Exception) {
                    e.printStackTrace()
                    // Exception prints failure
                    Toast.makeText(this@MainActivity2, "Encryption Failure!", Toast.LENGTH_LONG)
                        .show()
                }
                currText = textViewField.text.toString()
            }
            else{
                Toast.makeText(this@MainActivity2, "Incorrect Password!", Toast.LENGTH_LONG)
                    .show()
            }
        }


        // Save Button
        // Must be hit before encryption
        var textHolder = ""
        val saveBut = findViewById<View>(R.id.save) as Button
        saveBut.setOnClickListener {
            if (pwdHolder == requiredPwd) {
                try {

                    textHolder = textViewField.text.toString()

                } catch (e: Exception) {
                    e.printStackTrace()
                    // Exception prints failure
                    Toast.makeText(this@MainActivity2, "Save Failure!", Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(this@MainActivity2, "Incorrect Password!", Toast.LENGTH_LONG)
                    .show()
            }
        }

        // Password Button
        // Invisible, just below Encrypt & Decrypt buttons
        val pwdBut = findViewById<View>(R.id.passwordField) as Button
        pwdBut.setOnClickListener {
            try {
                val alertDialog = AlertDialog.Builder(this).create()
                alertDialog.setMessage("Password")
                var pwdInputField = EditText(this)
                alertDialog.setView(pwdInputField)

                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                    DialogInterface.OnClickListener { dialog, which ->
                        // Storing password for comparison in other buttons
                        pwdHolder = pwdInputField.text.toString()
                    })

                alertDialog.show()

                // Sets OK button centered
                val btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
                layoutParams.weight = 10f

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
//        userInput.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD

        // Encrypt Button
        val encryptBut = findViewById<View>(R.id.Encrypt) as Button
        encryptBut.setOnClickListener {
            println("pwd holder: $pwdHolder")
            println("required pwd: $requiredPwd")
            if (pwdHolder == requiredPwd) {
                try {
                    // Generate a key from password, encrypt text with it
                    cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec)
                    cipherText = cipher.doFinal(textHolder.toByteArray())

                    var str = String(cipherText)
                    textViewField.text = str

                } catch (e: Exception) {
                    e.printStackTrace()
                    // Exception prints failure
                    Toast.makeText(this@MainActivity2, "Encryption Failure!", Toast.LENGTH_LONG)
                        .show()
                }
            }
            else{
                Toast.makeText(this@MainActivity2, "Incorrect Password!", Toast.LENGTH_LONG)
                    .show()
            }
        }

        // DECRYPT BUTTON
        val decBut = findViewById<View>(R.id.Decrypt) as Button
        decBut.setOnClickListener {
            if (pwdHolder == requiredPwd) {
                try {
                    cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec)
                    val decrText: ByteArray = cipher.doFinal(cipherText)
                    val decryptString = String(decrText)
                    println("decrypted text: $decryptString")
                    textViewField.text = decryptString

                } catch (e: Exception) {
                    e.printStackTrace()

                    // Exception prints failure
                    Toast.makeText(this@MainActivity2, "Decryption Failed!", Toast.LENGTH_LONG)
                        .show()
                }
            }
                else{
                    Toast.makeText(this@MainActivity2, "Incorrect Password!", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }

}
