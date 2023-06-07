#! /bin/bash

## https://github.com/arthurpicht/installGradleWrapperWithoutGradle

gradle_version="6.9.4"

__dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

if [ -f "${__dir}/gradlew" ]; then
    echo 'Error: gradle wrapper already installed.' >&2
    exit 1
fi

if ! [ -x "$(command -v wget)" ]; then
  echo 'Error: wget is not installed.' >&2
  exit 1
fi

if ! [ -x "$(command -v java)" ]; then
  echo 'Error: java is not installed.' >&2
  exit 1
fi

mkdir -p ${__dir}/gradle/wrapper
cd ${__dir}/gradle/wrapper

wget https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar
wget https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.properties

cd ${__dir}
java -cp gradle/wrapper/gradle-wrapper.jar org.gradle.wrapper.GradleWrapperMain init

if [ -z "$gradle_version" ]
    then
        java -cp gradle/wrapper/gradle-wrapper.jar org.gradle.wrapper.GradleWrapperMain wrapper
    else
        java -cp gradle/wrapper/gradle-wrapper.jar org.gradle.wrapper.GradleWrapperMain wrapper --gradle-version ${gradle_version}
fi

# ./gradlew --version
