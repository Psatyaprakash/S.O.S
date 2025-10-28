FROM ghcr.io/cirruslabs/android-sdk:35

RUN useradd -m developer
USER developer
WORKDIR /home/developer/workspace

ENV GRADLE_USER_HOME=/home/developer/.gradle
ENV ANDROID_SDK_ROOT=/home/developer/Android/Sdk
ENV PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools

CMD ["/bin/bash"]
