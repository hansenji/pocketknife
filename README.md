Pocket Knife
============

Intent and Bundle "injection" library for Android which uses annotation processing to generate boilerplate code
for you.

```java
class ExampleActivity extends Activity {

}
```

For documentation and additional information see [the website][4].

__Remember: A pocket knife is like [a dagger][1] or [a butter knife][2] but has more gadgets.__



Download
--------

Download [the latest JAR][3] or grab via Maven:
```xml
<dependency>
  <groupId>com.vikingsen</groupId>
  <artifactId>pocketknife</artifactId>
  <version>1.0.0</version>
</dependency>
<dependency>
  <groupId>com.vikingsen</groupId>
  <artifactId>pocketknife-compiler</artifactId>
  <version>1.0.0</version>
  <scope>provided</scope>
</dependency>
```
or Gradle:
```groovy
compile 'com.vikingsen:pocketknife:1.0.0'
provided 'com.vikingsen:pocketknife-compiler:1.0.0'
```


License
-------

    Copyright 2014 Jordan Hansen

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.



 [1]: http://square.github.com/dagger/
 [2]: http://jakewharton.github.com/butterknife/
 [3]: {maven-central repo}
 [4]: {website}
