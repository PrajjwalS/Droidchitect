
# How to sniff blackstar amps packets on linux machine.

1. Install and setup Wireshark / Tshark
2. Load usbmon Kernel Module
	`sudo modprobe usbmon`
	Verify it got loaded
	`lsmod | grep usbmon`
3. Check if usbmon interface is exposed
    `tshark -D    # This should show usbmon0 usbmon1 <as per busses>`
 4. Find out which bus and device to connect to
     >$ lsusb | grep Blackstar
     >   Bus 001 Device 006: ID 27d4:0013 Blackstar Amplification Limited ID:Core v4

      Note: above means use usbmon1 and device is 6
4. Start tshark listening to the device on the correct bus 
 `sudo tshark -i usbmon1 -Y  'usb.device_address == 6 && usb.transfer_type == 0x01' -T fields -e frame.time_relative -e usb.endpoint_address.direction -e usb.capdata`
 
6. Make changes and see the packets here
