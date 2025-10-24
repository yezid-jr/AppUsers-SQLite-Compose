package com.example.app2usingdb

import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.app2usingdb.ui.theme.App2UsingDBTheme
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box


class MainActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseOpenHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        dbHelper = DatabaseOpenHelper(this)
        setContent {
            App2UsingDBTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    addUsers(dbHelper, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    App2UsingDBTheme {
        Greeting("Android")
    }
}

@Composable
fun addUsers(dbHelper: DatabaseOpenHelper, modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    var gender by remember { mutableStateOf("") }
    val genderOptions = listOf("Male", "Female", "Other")
    var expanded by remember { mutableStateOf(false) }

    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var editingUserId by remember { mutableStateOf<Int?>(null) }
    var users by remember { mutableStateOf(dbHelper.getAllUsers()) }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = lastname,
            onValueChange = { lastname = it },
            label = { Text("Lastname") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box {
            OutlinedTextField(
                value = gender,
                onValueChange = {},
                readOnly = true,
                label = { Text("Gender") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Expand Menu"
                        )
                    }
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                genderOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            gender = selectionOption
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val ageInt = age.toIntOrNull()
                if (name.isNotBlank() && lastname.isNotBlank() && ageInt != null &&
                    gender.isNotBlank() && phone.isNotBlank() && email.isNotBlank()) {

                    if (editingUserId == null) {
                        // Agregar nuevo usuario
                        if (dbHelper.insertUser(name, lastname, ageInt, gender, phone, email)) {
                            Toast.makeText(context, "User added successfully", Toast.LENGTH_SHORT).show()
                            users = dbHelper.getAllUsers()
                            // Limpiar campos
                            name = ""
                            lastname = ""
                            age = ""
                            gender = ""
                            phone = ""
                            email = ""
                        } else {
                            Toast.makeText(context, "Failed to add user", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Actualizar usuario existente
                        if (dbHelper.updateUser(editingUserId!!, name, lastname, ageInt, gender, phone, email)) {
                            Toast.makeText(context, "User updated successfully", Toast.LENGTH_SHORT).show()
                            users = dbHelper.getAllUsers()
                            editingUserId = null
                            // Limpiar campos
                            name = ""
                            lastname = ""
                            age = ""
                            gender = ""
                            phone = ""
                            email = ""
                        } else {
                            Toast.makeText(context, "Failed to update user", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (editingUserId == null) "Add User" else "Update User")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Users List:", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        users.forEach { user ->
            UserRow(
                user = user,
                onDelete = {
                    if (dbHelper.deleteUser(user["id"] as Int)) {
                        Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show()
                        users = dbHelper.getAllUsers()
                    } else {
                        Toast.makeText(context, "Failed to delete user", Toast.LENGTH_SHORT).show()
                    }
                },
                onEdit = {
                    editingUserId = user["id"] as Int
                    name = user["name"] as String
                    lastname = user["lastname"] as String
                    age = user["age"].toString()
                    gender = user["gender"] as String
                    phone = user["phone"] as String
                    email = user["email"] as String
                }
            )
        }
    }
}

@Composable
fun UserRow(user: Map<String, Any>, onDelete: () -> Unit, onEdit: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text("Name: ${user["name"]}")
        Text("Lastname: ${user["lastname"]}")
        Text("Age: ${user["age"]}")
        Text("Gender: ${user["gender"]}")
        Text("Phone: ${user["phone"]}")
        Text("Email: ${user["email"]}")

        Row {
            Button(onClick = onEdit) {
                Text("Edit")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onDelete) {
                Text("Delete")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

class DatabaseOpenHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "user_database.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_LASTNAME = "lastname"
        private const val COLUMN_AGE = "age"
        private const val COLUMN_GENDER = "gender"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_EMAIL = "email"

        private const val CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_LASTNAME TEXT NOT NULL,
                $COLUMN_AGE INTEGER NOT NULL,
                $COLUMN_GENDER TEXT NOT NULL,
                $COLUMN_PHONE TEXT NOT NULL,
                $COLUMN_EMAIL TEXT NOT NULL
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertUser(
        name: String,
        lastname: String,
        age: Int,
        gender: String,
        phone: String,
        email: String
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_LASTNAME, lastname)
            put(COLUMN_AGE, age)
            put(COLUMN_GENDER, gender)
            put(COLUMN_PHONE, phone)
            put(COLUMN_EMAIL, email)
        }
        return try {
            val result = db.insert(TABLE_NAME, null, values)
            db.close()
            result != -1L
        } catch (e: Exception) {
            db.close()
            false
        }
    }

    fun getAllUsers(): List<Map<String, Any>> {
        val db = readableDatabase
        val usersList = mutableListOf<Map<String, Any>>()

        val cursor = db.query(
            TABLE_NAME,
            arrayOf(
                COLUMN_ID, COLUMN_NAME, COLUMN_LASTNAME, COLUMN_AGE,
                COLUMN_GENDER, COLUMN_PHONE, COLUMN_EMAIL
            ),
            null,
            null,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val user = mapOf(
                    COLUMN_ID to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    COLUMN_NAME to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    COLUMN_LASTNAME to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LASTNAME)),
                    COLUMN_AGE to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE)),
                    COLUMN_GENDER to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)),
                    COLUMN_PHONE to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                    COLUMN_EMAIL to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                )
                usersList.add(user)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return usersList
    }

    fun deleteUser(userId: Int): Boolean {
        val db = writableDatabase
        return try {
            val result = db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(userId.toString()))
            db.close()
            result > 0
        } catch (e: Exception) {
            db.close()
            false
        }
    }

    fun updateUser(
        userId: Int,
        name: String,
        lastname: String,
        age: Int,
        gender: String,
        phone: String,
        email: String
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_LASTNAME, lastname)
            put(COLUMN_AGE, age)
            put(COLUMN_GENDER, gender)
            put(COLUMN_PHONE, phone)
            put(COLUMN_EMAIL, email)
        }
        return try {
            val result = db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(userId.toString()))
            db.close()
            result > 0
        } catch (e: Exception) {
            db.close()
            false
        }
    }
}