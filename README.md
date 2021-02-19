Android StackBlur
=================

Android StackBlur is a library that can perform a blurry effect on a Bitmap based on a gradient or radius, and return the result. The library is based on the code of Mario Klingemann. 

The library is compatible for Android versions 1.5 (so pretty much compatible with every device).

A [library][1] and a [sample application][2] are provided with the code.

![Example Image][3]
![Example Image][4]


Acknowledgements
--------------------
* [Mario Klingemann][5] developed the original algorithm and gave me the idea to port it to Android.
* [Manuel Peinado Gallego][6] has generously pointed me out some other sources working on the same area.
* [Nicolas Pomepuy][7] pointed me out to his really useful article on the topic.
* [Dr-Emann][9] for his contribution adding RenderScript and Muiltithreding support.

Usage
--------------------
Download the library with git. When it has been imported into your project. First initialize your StackBlurManager to load a sample image:
```java
    stackBlurManager = new StackBlurManager(getBitmapFromAsset(this, "android_platform_256.png"));
```
Process using a certain radius with the following line:
```java
    stackBlurManager.process(progress*5);
```
and finally obtain the image and load it into an ImageView or any other component:
```java
    imageView.setImageBitmap(_stackBlurManager.returnBlurredImage() );
```
If you want to use the native code (NDK), first you need to compile the native files. Move to the folder where you have the project, and call:
```bash    
    ndk-build
```
If everything goes alright, you will compile and generate a library object, libblur.so, in the folder libs. For more information regarding the NDK framework, please [click here][8]. Then, from the code you just need to code:
```java
    stackBlurManager.processNatively(progress*5);
```
The function processNatively(int radius) return the image already blurry. This code is 25-30 times faster as the Java code.

If you want to use RenderScript, you can do it by using:
```java
   stackBlurManager.processRenderScript(progress*5);
```

Version history
--------------------
* 10.02.2014: Added Multithreading support thanks to [Dr-Emann][9]
* 27.01.2014: Added Benchmarking, bug fixing thanks to [Dr-Emann][9]
* 10.01.2014: Added RenderScript support thanks to [Dr-Emann][9]
* 04.12.2013: Added support for NDK blurry, much faster.
* 02.12.2013: Solved the issue #1, provoking an ArrayOutOfBoundsException.
* 08.09.2013: Added support for Gradle
* 19.08.2013: Added support for Alpha blurring

Next steps
--------------------
As soon as I get some free time I want to add the following features:

* Uploading to MavenCentral
* Support for PNG9
* Unit tests

If you want to colaborate with the project, feel free to submit a pull request! 

Also, if you have used Android StackBlur on your app and you let me know, I can link it from here :)

Developed By
--------------------

Enrique López Mañas - <eenriquelopez@gmail.com>

<a href="https://twitter.com/eenriquelopez">
  <img alt="Follow me on Twitter"
       src="https://raw.github.com/kikoso/android-stackblur/master/art/twitter.png" />
</a>
<a href="https://plus.google.com/103250453274111396206">
  <img alt="Follow me on Google+"
       src="https://raw.github.com/kikoso/android-stackblur/master/art/google-plus.png" />
</a>
<a href="http://de.linkedin.com/pub/enrique-l%C3%B3pez-ma%C3%B1as/15/4a9/876">
  <img alt="Follow me on LinkedIn"
       src="https://raw.github.com/kikoso/android-stackblur/master/art/linkedin.png" />

[1]: https://github.com/kikoso/android-stackblur/tree/master/StackBlur
[2]: https://github.com/kikoso/android-stackblur/tree/master/StackBlurDemo
[3]: https://raw.github.com/kikoso/android-stackblur/master/art/screenshot1.png
[4]: https://raw.github.com/kikoso/android-stackblur/master/art/screenshot2.png
[5]: http://www.quasimondo.com/
[6]: https://twitter.com/mpg2
[7]: http://nicolaspomepuy.fr/
[8]: http://developer.android.com/tools/sdk/ndk/index.html
[9]: https://github.com/Dr-Emann
