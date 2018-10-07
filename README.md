# RGB LED controlling with Android Things on Raspberry Pi 3 B
## Features
* Using three pins to control an RGB LED displaying in different colors.
* Get input from a button and then change the pace of color displaying (ex 1). Forexample, the RGB LED changes colors in 2s by default. After a button is pressed, the rate will change to 1s, then 0.5s, 0.1s and back to 2s.
*  Similar to exercise 1, control the RGB LED by using PWM to change the brightness of the LED.
* Get input from a button and change the brightness of each color of the RGB LED. For example, the RGB LED changes the brightness of red, green, blue by default. After a button is pressed, only the red one is changing its brightness, then green, blue and back to three colors.
* Blink each LED in different paces. The red LED is blinking every 0.5s, the green is 2s and the blue is 3s.
## Connection
| Raspberry PI 3 | Peripherals   | 
| -------------- | -------------:| 
| BCM2           | LED Red       | 
| BCM3           | LED Green     | 
| BCM4           | LED Blue      | 
| BCM21          | Button        | 
* Please note that based on your LED, the LED's common pin might be connected to Vcc or GND.
* Also keep in mind that Button's pins must be wired so that it is active low (using a resistor might help).
## Usage
* There are 5 java source code files (which can be found under [app/src/main/java/com/example/duchoang/blinkingled](https://github.com/hoangnguyenminhduc/ledRGB_with_AndroidThings/tree/master/app/src/main/java/com/example/duchoang/blinkingled)). Each file includes a full implementation of one of the aforementioned features respectively. 
* Just copy any into the **MainActivity.java** and run.
## Members
1. Minh-Duc Hoang-Nguyen, 1610755
2. Thanh-Linh Thai-Thi, 1611830
3. Thinh Le-Duc, 1613346
4. Thinh Le-Minh, 1513249

## Info
Bach Khoa University, Faculty of Computer Science and Enginnering.
Computer Engineering Department.
IoT Application Development - Fall 2018
### Instructors
1. Le Trong Nhan, Ph.D
2. Nguyen Tran Huu Nguyen, Ph.D

