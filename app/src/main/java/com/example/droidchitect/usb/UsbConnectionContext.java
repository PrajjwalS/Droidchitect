package com.example.droidchitect.usb;

import android.hardware.usb.*;

public class UsbConnectionContext {
    public UsbDevice device;
    public UsbDeviceConnection connection;
    public UsbInterface usbInterface;
    public UsbEndpoint endpointIn;
    public UsbEndpoint endpointOut;
}