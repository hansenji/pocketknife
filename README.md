[![Build Status](https://travis-ci.org/hansenji/pocketknife.svg?branch=master)](https://travis-ci.org/hansenji/pocketknife)

Pocket Knife
============

Intent and Bundle "injection" library for Android which uses annotation processing to generate boilerplate code
for you. This library is based on [ButterKnife][2] and [Dagger][1]

```java
class ExampleActivity extends Activity {
  @InjectExtra("EXTRA_ID")
  int id;
  
  @SaveState
  int listPosition;
  
  public void onCreate(Bundle savedInstanceState) {
    ...
    PocketKnife.injectExtras(this);
    PocketKnife.restoreInstanceState(this, savedInstanceState);
    ...
  }
  
  public void onSaveInstanceState(Bundle outState) {
    ...
    PocketKnife.saveInstanceState(this, outState);
    ...
  }
}
```

For documentation and additional information see [the website][5].

__Remember: A pocket knife is like [a dagger][1] or [a butter knife][2] but has more gadgets.__



Download
--------

Download the latest JARs [core][3] and [compiler][4] or grab via Maven:
```xml
<dependency>
  <groupId>com.vikingsen</groupId>
  <artifactId>pocketknife-core</artifactId>
  <version>0.5.1</version>
</dependency>
<dependency>
  <groupId>com.vikingsen</groupId>
  <artifactId>pocketknife-compiler</artifactId>
  <version>0.5.1</version>
  <scope>provided</scope>
</dependency>
```
or Gradle:
```groovy
compile 'com.vikingsen:pocketknife-core:0.5.1'
provided 'com.vikingsen:pocketknife-compiler:0.5.1'
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
 [3]: http://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.vikingsen&a=pocketknife-core&v=LATEST
 [4]: http://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.vikingsen&a=pocketknife-core&v=LATEST
 [5]: http://hansenji.github.io/pocketknife
