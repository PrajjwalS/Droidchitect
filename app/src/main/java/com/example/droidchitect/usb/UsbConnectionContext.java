package com.example.droidchitect.usb;

import android.hardware.usb.*;

public class UsbConnectionContext {
    public UsbDevice device;
    public UsbDeviceConnection connection;
    public UsbInterface usbInterface;
    public UsbEndpoint endpointIn;
    public UsbEndpoint endpointOut;

    public UsbConnectionContext(UsbDevice device, UsbDeviceConnection connection, UsbInterface targetInterface, UsbEndpoint endpointIn, UsbEndpoint endpointOut) {
        this.device = device;
        this.connection = connection;
        this.usbInterface = targetInterface;
        this.endpointIn = endpointIn;
        this.endpointOut = endpointOut;
    }
}