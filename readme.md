# dingzOpenHabHelperApi

### ready to use jar in '/out/artifacts/dingzOpenHabHelperApi_jar/'
The standalone jar in '/out/artifacts/dingzOpenHabHelperApi_jar/' is ready to use.  

### define 'properties.config'
A config file in the same directoy as the main program is required.  
For local testing place file in root directory of the project.
Name: properties.config  
  
Content:
```
exposeApiName=http://0.0.0.0:8000/
openhabHostname=http://10.0.1.x:8080
allowedDevices=ANY,F008D1C4D124,F008D1C4D125
allowedIps=ANY,10.0.1.25
Token=yourOpenHabApiToken
ResetMode=true
ResetValue=0
```

### run configuration for local test
```
add new Configuration (+)

Name: dingzOpenHabHelperApi
Main Class: dingzOpenHabHelperApi
Working Directory: e.g. /Users/remo/Documents/workspace/dingzOpenHabHelperApi
Use classpath of module: dingzOpenHabHelperApi
```

**URL's**
```
http://localhost:8000/itemName?mac=F008D1C4D124&index=3&action=2

config:
http://localhost:8000/application.wadl?detail=true
```
parameter | description |
--- | --- |
itemName | the itemname in OpenHAB |
mac | must be in properties.config, or ANY, otherwise call will be rejected |
index | not used, but is sent by dingz API call |
action | update string for OpenHAB item |


### build standalone jar
**setup**

Menu **File** >> **Project Structure** >> **Artfacts** >> **+** >> **JAR** >> **From modules with dependencies**  
Select **Mail class**  
```
  ch.hakuna.DingzOpenHabHelperApi
```
adjust **Directory for META-INF/MANIFEST.MF**  
replace
```  
  ..\main\java
```
  with  
```
  ..\main\resources
```
  
**build/update**
- Menu **Build** >> **Build Artifacts** >> Action **Build**
- Then copy **dingzOpenHabHelperApi.jar** to target server /usr/share/dingzOpenHabHelperApi/
  
**start standalone**  
``` 
/usr/bin/java -jar /usr/share/dingzOpenHabHelperApi/dingzOpenHabHelperApi.jar
```
  
### Linux Service  
Create user:
```
groupadd -r dingzhelper  
useradd -r -s /bin/false -g dingzhelper dingzhelper
```
Prepare directory:
```
mkdir /usr/share/dingzOpenHabHelperApi
```
Start script (dingzOpenHabHelperApiStart.sh):
```
#!/bin/bash
sudo -H -u dingzhelper bash -c '/usr/bin/java -jar /usr/share/dingzOpenHabHelperApi/dingzOpenHabHelperApi.jar &'
```
Stop script (dingzOpenHabHelperApiStop.sh):
```
#!/bin/bash
kill -9 `ps -ef | grep dingzOpenHabHelperApi.jar | grep -v grep | awk '{print $2}'`
rm -Rf /tmp/dingzOpenHabHelperApi.log
```
properties.config
```
openhabHostname=http://10.0.1.x:8080
allowedDevices=ANY,F008D1C4D124,F008D1C4D125
allowedIps=ANY,10.0.1.25
Token=yourOpenHabApiToken
ResetMode=true
ResetValue=0
```
Adjust permissions:
```
chown -R dingzhelper:dingzhelper /usr/share/dingzOpenHabHelperApi
```
Service (/etc/systemd/system/dingz.service):
```
[Unit]
Description=dingz
After=openhab.service

[Service]
StandardOutput=file:/tmp/dingzOpenHabHelperApi.log
StandardError=inherit
WorkingDirectory=/usr/share/dingzOpenHabHelperApi
ExecStart=/usr/share/dingzOpenHabHelperApi/dingzOpenHabHelperApiStart.sh
ExecStop=/usr/share/dingzOpenHabHelperApi/dingzOpenHabHelperApiStop.sh
SuccessExitStatus=143
Type=forking
RemainAfterExit=yes
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
```
Enable/start service (output in /tmp/dingzOpenHabHelperApi.log):
```
systemctl enable dingz.service
systemctl start dingz
```

### logfile output samples
```
dd.mm.yyyy HH:MM:SS - INFO:  Jersey server starting at http://0.0.0.0:8000/
dd.mm.yyyy HH:MM:SS - INFO:  Jersey server started at http://0.0.0.0:8000/
dd.mm.yyyy HH:MM:SS - INFO:  Update item 'itemName' to value 'action' from device 'mac(index)' successfully sent.
dd.mm.yyyy HH:MM:SS - ERROR: File 'properties.config' not found.
dd.mm.yyyy HH:MM:SS - ERROR: Commands from device mac not allowed.
dd.mm.yyyy HH:MM:SS - ERROR: Update item 'itemName' to value 'action' from device 'mac(index)' failed with HTTPXXX.
dd.mm.yyyy HH:MM:SS - ERROR: OpenHAB not available.
```
