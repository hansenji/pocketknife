Change Log
==========

Version 2.6.0 *(2015-06-17)*
----------------------------

  * Fixed Issue #4 added @Key annotation to specify Extra/Argument keys in builders.

Version 2.5.1 *(2015-05-26)*
----------------------------

  * Fixed ClassDefNotFound build exception of android classes

Version 2.5.0 *(2015-05-25)*
----------------------------

  * Added @FragmentBuilder
  * Allow all @*Builder annotations to be apart of the same interface
  * Moved @IntentBuilder path parameter to its own annotation @Data
  
Version 2.0.1 *(2015-04-29)*
----------------------------

  * Fixed bug where argument and extra keys couldn't contain '.' (Every builtin extra or argument key in android) 
  
Version 2.0.0 *(2015-04-01)*
----------------------------

  * Generate default extra and argument keys based on field names
  * Added Intent and Bundle Builders
  
Version 1.1.3 *(2015-03-18)*
----------------------------

 * Fixed bug with injection default values
  
Version 1.1.2 *(2015-03-04)*
----------------------------

  * Fixed bug with annotating the same field with @SaveState and @InjectArgument
  
Version 1.1.1 *(2015-02-26)*
----------------------------
  
  * Fixed Inheritance bug (No adapter for child class)

Version 1.1.0 *(2015-02-01)*
----------------------------

  * Made IntentBinding and BundleBinding an interface. Not compatible with version 1.0.x
  * Works with class inheritance
  
Version 1.0.1 *(2015-01-20)*
----------------------------

  * Fixed AnnotationProcessor not running on @InjectExtra annotations
   
Version 1.0.0 *(2014-11-19)*
----------------------------

  * Initial Release