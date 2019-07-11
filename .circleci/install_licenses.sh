#!/bin/bash
# Copies the accepted licenses from the repository (`.circleci/licenses`) to the Android SDK accepted licenses folder.
#
# When Google changes the Android SDK licenses, the builds on CircleCI fail, because the new licenses need to be
# accepted.
# To avoid this, we save the accepted licenses into the repository and install them to the CircleCI's Android SDK at
# build time with this script. The accepted licenses are located in the folder `.circleci/licenses`. In that folder,
# there are files which contain a hash of the accepted licenses.
# More infos: http://d.android.com/r/studio-ui/export-licenses.html
#
# When builds are failing again with this error message:
# `Failed to install the following Android SDK packages as some licences have not been accepted.`
# You have to add another license, by doing this:
# 1. Connect to the Docker container via SSH.
# 2. Navigate to the Android SDK's `licenses` folder with `cd $ANDROID_HOME/licenses/`
# 3. Run `sdkmanager --update` and accept the new license(s)
# 4. Find the changed file(s). To list the files sorted by their changed date run `ls -lt`.
# 5. Copy the changed file(s) to the repository's `.circleci/licenses` folder.
#    One way to do this is simply output the file content with e.g. `cat android-sdk-license`,
#    and copy & paste the file content to the local file `.circleci/licenses/android-sdk-license`.
# 6. Commit and push the updated license file
# 7. The build works again!

cp -rf .circleci/licenses/* $ANDROID_HOME/licenses/