# .idx/dev.nix
{ pkgs, ... }: {
  channel = "stable-25.05";

  packages = [
    pkgs.jdk21
    pkgs.unzip
    pkgs.zlib
  ];

  env = {
    JAVA_HOME = "${pkgs.jdk21}";
    ANDROID_HOME = "/home/user/.androidsdkroot";
    GRADLE_OPTS = "-Dorg.gradle.java.home=${pkgs.jdk21} -Dorg.gradle.jvmargs='-Xmx2048m' -Dfile.encoding=UTF-8";
  };

  idx = {
    extensions = [
      "fwcd.kotlin"
      "amazonwebservices.amazon-q-vscode"
      "bernabe.gemcommit"
      "redhat.java"
      "vscjava.vscode-gradle"
      "vscjava.vscode-java-debug"
      "vscjava.vscode-java-dependency"
      "vscjava.vscode-java-pack"
      "vscjava.vscode-java-test"
      "vscjava.vscode-maven"
    ];

    # Секция previews полностью удалена.
    # IDX больше не будет пытаться запустить эмулятор или веб-сервер.

    workspace = {
      onCreate = {
        # Этот скрипт оставляем, он нужен, чтобы скачать Android SDK
        # иначе Gradle не сможет собрать проект даже в терминале.
        fix-sdk = ''
          rm -rf /home/user/.androidsdkroot/build-tools/35.0.0
          rm -rf /home/user/.androidsdkroot/ndk-bundle
          /home/user/.androidsdkroot/cmdline-tools/latest/bin/sdkmanager "build-tools;36.0.0" "platforms;android-36"
          chmod +x gradlew
          ./gradlew dependencies --no-daemon
        '';
      };
    };
  };
}