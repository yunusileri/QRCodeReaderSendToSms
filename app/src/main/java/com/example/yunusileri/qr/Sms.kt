package com.example.yunusileri.qr

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.contact_view.*
import java.io.IOException
import android.content.Intent


class Sms : AppCompatActivity() {
    private lateinit var messageText: EditText
    private lateinit var numberText: EditText
    private lateinit var sendButton: Button
    private lateinit var contactDialog: Dialog
    private lateinit var btnContact: Button


    private val contactList = ArrayList<Contact>()
    private val sortedContactList = ArrayList<Contact>()


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            0 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        return
                    }
                    try {
                        sendSms(numberText.text.toString(), messageText.text.toString())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
            1 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        return
                    }
                    try {
                        contactDialogs()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms)

        messageText = findViewById(R.id.messageShow)
        numberText = findViewById(R.id.etPersons)
        sendButton = findViewById(R.id.btnSend)
        btnContact = findViewById(R.id.btnContact)
        val btnShare: Button = findViewById(R.id.btnShare)
        val intent = this.intent.extras
        messageText.setText(intent?.getString("1", ""))


        sendButton.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(applicationContext, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@Sms, arrayOf(Manifest.permission.SEND_SMS), 0)
            } else {
                if (numberText.text.toString()  != "" ){
                    sendSms(numberText.text.toString(), messageText.text.toString())
                }
                else{
                    Toast.makeText(this,"Number can't be empty",Toast.LENGTH_SHORT).show()
                }

            }
        }
        btnContact.setOnClickListener {
            contactDialogs()
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 1)

        } else {
            getContactsData()
            val tempList = contactList.sortedWith(compareBy({ it.name }, { it.phoneNumber }))
            sortedContactList.addAll(tempList)

            }

        btnShare.setOnClickListener{
           val smsIntent=Intent()
            smsIntent.action = Intent.ACTION_SEND
            smsIntent.putExtra(Intent.EXTRA_TEXT, messageText.text.toString())
            smsIntent.type = "text/plain"
            startActivity(smsIntent)

        }
    }

    private fun sendSms(number: String, message: String) {
        val sms = SmsManager.getDefault()
        sms.sendTextMessage(number, null, message, null, null)
        Toast.makeText(this, "Send To Sms", Toast.LENGTH_SHORT).show()
        messageText.setText("")
        numberText.setText("")
    }

    private fun contactDialogs() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 1)

        } else {
            contactDialog = Dialog(this, android.R.style.Theme_NoTitleBar_Fullscreen)
            contactDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
            contactDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            contactDialog.setContentView(R.layout.contact_view)
            contactDialog.setCancelable(true)
            contactDialog.show()

            val adapter = ListAdapter(this, sortedContactList)

            contactDialog.contacts_list.adapter = adapter

            contactDialog.contacts_list.setOnItemClickListener { _, _, position, _ ->


                numberText.setText(adapter.getItem(position).phoneNumber)
                contactDialog.dismiss()


            }

        }
    }

    private fun getContactsData() {
        val contactCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

        if ((contactCursor?.count ?: 0) > 0) {

            while (contactCursor != null && contactCursor.moveToNext()) {
                val rowID = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts._ID))

                val name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                var phoneNumber = ""
                if (contactCursor.getInt(contactCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val phoneNumberCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf<String>(rowID),
                            null
                    )
                    while (phoneNumberCursor?.moveToNext()!!) {
                        phoneNumber += phoneNumberCursor.getString(
                                phoneNumberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        ) + "\n"
                    }
                    phoneNumberCursor.close()
                }
                contactList.add(Contact(name, phoneNumber))
            }
        }
        contactCursor?.close()
    }
}
