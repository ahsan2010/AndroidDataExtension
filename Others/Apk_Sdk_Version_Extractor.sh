#!/bin/bash
apkInputArchiveFolderName=/safwatscratch/shassan/Input_Apks/
scriptLogFolderName=/home/local/SAIL/ahsan/Documents/Sdk_version/Safwat_Approach/SDK_Version_Folder
aaptTool=/home/local/SAIL/ahsan/Android/Sdk/build-tools/29.0.2/aapt

echo "Start Getting SDK and Apk version for the Apks in the folder $apkInputArchiveFolderName "
echo "Release_Name,Application,Date,Version_Name,Version_Code,Min_SDK_Version,Target_SDK_Version,Max_SDK_Version" >"$scriptLogFolderName/SDK_Versions_Report.csv"
for apkFileName in `find $apkInputArchiveFolderName -name "*.apk" | sort`
do
    echo "Collecting data from Apk file: [$apkFileName] "
    searchText="$apkInputArchiveFolderName/"
    releaseName=`echo ${apkFileName//$searchText/}`

    applicationName=`echo $releaseName | cut -f1 -d-`
    versionCode=`echo $releaseName | cut -f2 -d-`
    
    releaseDate=`echo $releaseName | cut -f3 -d-`
    searchText=".apk"
    releaseDate=`echo ${releaseDate//$searchText/}`
    searchText="_"
    releaseDate=`echo ${releaseDate//$searchText/.}`
    maxSdkVersion=0

    "$aaptTool" d badging "$apkFileName"  | head -3  >"$scriptLogFolderName/temp.out" 2>>"$scriptLogFolderName/error.log"    
    if [ "$?" == 0 ]; then
    	
    	delimiter="'"
        minSDKVersion=`"$aaptTool" d badging "$apkFileName"  | grep "sdkVersion:" | cut -f2 -d$delimiter | head -1 `
        
        if [ -z "${minSDKVersion}" ]; then
            echo "The first approach could not determine the sdk version so the System is trying to use another approach for file [$releaseName]."
            delimiter=")"        
            minSDKVersion=`"$aaptTool" l -a "$apkFileName" | grep minSdkVersion | cut -f3 -d$delimiter | head -1 `
            # Convert the value from Hexa decimal to decimal value.
            minSDKVersion=`printf "%d\n" $minSDKVersion`
            
            targetSDKVersion=`"$aaptTool" l -a "$apkFileName" | grep targetSdkVersion | cut -f3 -d$delimiter | head -1 `

            maxSdkVersion=`"$aaptTool" l -a "$apkFileName" | grep maxSdkVersion | cut -f3 -d$delimiter | head -1 `
            maxSdkVersion=`printf "%d\n" $maxSdkVersion`

            if [ -z "${targetSDKVersion}" ]; then
                targetSDKVersion=$minSDKVersion
            else
                targetSDKVersion=`printf "%d\n" $targetSDKVersion`
            fi
            
        else
            delimiter="'"        
            targetSDKVersion=`"$aaptTool" d badging "$apkFileName" | grep "targetSdkVersion:" | cut -f2 -d$delimiter | head -1 `
            maxSdkVersion=`"$aaptTool" d badging "$apkFileName" | grep "maxSdkVersion:" | cut -f2 -d$delimiter | head -1 `
            
            if [ -z "${targetSDKVersion}" ]; then
                targetSDKVersion=$minSDKVersion
            fi
        fi
        delimiter="'"
        versionName=`cat "$scriptLogFolderName/temp.out" | grep versionName | cut -f6 -d$delimiter  | cut -f1 -d ","`
        
        echo "---------   File [$releaseName] analyzed successfully -------- :) :) :)."
        echo "$releaseName,$applicationName,$releaseDate,$versionName,$versionCode,$minSDKVersion,$targetSDKVersion,$maxSdkVersion" >>"$scriptLogFolderName/SDK_Versions_Report.csv"
        echo "$releaseName" >>"$scriptLogFolderName/Success_SDK_Version_list.txt"
    else
        echo "---------   File [$releaseName] Can't be Analyzed  ---------- !!!!!!!!!"
        echo "$releaseName" >>"$scriptLogFolderName/Error_SDK_Version_list.txt"
    fi
done
echo "---------  System finished analyzing all the apks successfully :) Have a good day :) :) :) "