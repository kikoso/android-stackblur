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


Usage
--------------------
Download the library with git. When it has been imported into your project. First initialize your StackBlurManager to load a sample image:

    _stackBlurManager = new StackBlurManager(getBitmapFromAsset(this, "android_platform_256.png"));

Process using a certain radius with the following line:

    _stackBlurManager.process(progress*5);

and finally obtain the image and load it into an ImageView or any other component:

    _imageView.setImageBitmap(_stackBlurManager.returnBlurredImage() );

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
