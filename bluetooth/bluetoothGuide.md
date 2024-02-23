Using Bluetooth in an Android application to control a robot involves several key steps, including setting up the necessary permissions, discovering devices, pairing with the robot, and then sending commands to it. This tutorial will guide you through creating a basic Android app that can discover, connect, and send commands to a Bluetooth-enabled robot using Java in Android Studio (Chipmunk version). The UI will be simple, focusing on functionality.

### Resources
1. Android developer guide for Bluetooth: [https://developer.android.com/guide/topics/connectivity/bluetooth](https://developer.android.com/guide/topics/connectivity/bluetooth)
2. Javapoint Tutorial [https://www.javatpoint.com/android-bluetooth-tutorial](https://www.javatpoint.com/android-bluetooth-tutorial)
3. Reintech tutorial [https://reintech.io/blog/developing-android-app-bluetooth-iot-devices](https://reintech.io/blog/developing-android-app-bluetooth-iot-devices)

### Step 1: Setup Your Android Studio Project

1. **Create a New Project** in Android Studio.
2. Choose **Empty Activity**.
3. Name your project, for example, "BluetoothRobotController".
4. Ensure the language is set to **Java**.
5. Finish the project setup.

### Step 2: Update Manifest for Bluetooth Permissions

Open your `AndroidManifest.xml` and add the following permissions above the `<application>` tag:

```xml
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>

```

For services running on Android 10 and higher cannot discover Bluetooth devices unless they have the ACCESS_BACKGROUND_LOCATION permission. So in that case we need this permission as well:
```xml
<uses-permission android:name="android.permission. ACCESS_BACKGROUND_LOCATION"/>

```

For Android 6.0 (API level 23) and higher, you must request the location permission at runtime to discover Bluetooth devices. 

### Step 3: Design the UI with ConstraintLayout

1. Open the `activity_main.xml` file located in `res/layout`.
2. Use the Interface Builder to design your UI. For this example, you might add a Button to connect to the Bluetooth device and a few Buttons to send commands (forward, backward, left, right) to the robot.
3. Assign IDs to your components (`@+id/connectButton`, `@+id/forwardButton`, etc.).

Example of adding a Button in XML:

```xml
<Button
    android:id="@+id/connectButton"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Connect"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"/>
```

### Step 4: Request Runtime Permissions

In your `MainActivity.java`, request location permissions at runtime before starting the Bluetooth operations. This is required for device discovery. This code bellow works for Android < 10. 

```java
if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            MY_PERMISSIONS_REQUEST_LOCATION);
}
```
If you have Android > 10, use the following code:

```java
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    if (ContextCompat.checkSelfPermission(this,
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    PERMISSION_CODE)
        }
}```

Handle the permission result to proceed or abort Bluetooth operations based on the user's response.

### Step 5: Implement Bluetooth Operations

1. **Declare a BluetoothAdapter:**

```java
private BluetoothAdapter bluetoothAdapter;
```

2. **Initialize the BluetoothAdapter and Check if Bluetooth is Supported:**

In your `onCreate` method:

```java
bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
if (bluetoothAdapter == null) {
    Toast.makeText(this, "Bluetooth is not supported on this device.", Toast.LENGTH_LONG).show();
    finish(); // Automatically close the app or disable Bluetooth features.
}
```

3. **Enable Bluetooth:**

Still, in `onCreate` or in response to a user action (e.g., clicking the "Connect" button):

```java
if (!bluetoothAdapter.isEnabled()) {
    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
}
```

4. **Discover Devices or Pair with the Robot:**

You can start a discovery for nearby devices or list already paired devices. To connect to a known robot, listing paired devices is often sufficient:

```java
Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
if (pairedDevices.size() > 0) {
    for (BluetoothDevice device : pairedDevices) {
        String deviceName = device.getName();
        String deviceAddress = device.getAddress(); // MAC address
    }
}
```

5. **Connect to the Robot:**

Connecting to a Bluetooth device typically involves using a `BluetoothSocket` to create a communication channel. This part can get complex as it involves threading for managing connection attempts and data transfer.

```java
private class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;

    public ConnectThread(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        mmDevice = device;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used by the server code.
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        manageMyConnectedSocket(mmSocket);
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}
```

6. **Sending Commands to the Robot:**

After establishing a connection with the robot, you can use the output stream from the `BluetoothSocket` to send commands:

```java
private void manageMyConnectedSocket(BluetoothSocket mmSocket) {
    // Get the input and output streams; using temp objects because
    // member streams are final.
    InputStream tmpIn = null;
    OutputStream tmpOut = null;

    try {
        tmpOut = mmSocket.getOutputStream();
    } catch (IOException e) {
        Log.e(TAG, "Error occurred when creating output stream", e);
    }

    // Send a command to the connected device.
    String command = "forward"; // Example command
    byte[] bytes = command.getBytes();
    try {
        tmpOut.write(bytes);
    } catch (IOException e) {
        Log.e(TAG, "Error occurred when sending data", e);
    }
}
```

### Step 6: Run and Test Your App

1. **Build and run your app.** Ensure your target device's Bluetooth is turned on and it's discoverable or already paired with the robot.
2. **Use the app to connect and send commands** to the robot as intended.

### Conclusion

This tutorial outlined the steps to create a simple Android app to control a robot via Bluetooth using Java. Real-world applications might require more detailed implementation, especially for managing Bluetooth connections, threading, and data transfer. Always test Bluetooth functionalities with the actual hardware to ensure compatibility and performance.