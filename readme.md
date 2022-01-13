# dingzOpenHabHelperApi   
The API helper receives an API GET request without API token from any device and forwards it to OpenHAB via PUT request (Updates the state of an OpenHAB item). The OpenHAB API token wil be added to the request.  
Restrictions to the API can be done with query parameter mac=xxx and by source IP address.  
If no restrictions are required (not recommended), add ANY to the allowedDevices/allowedIps config.  
  
ONLY for OpenHAB3.x, for OpenHAB2.x you need to comment out these lines and rebuild standalone jar.    
class: OpenHabConnect
```
            httpPut.addHeader("Authorization", "Bearer " + OPENHAB_TOKEN);
            httpPut.addHeader("Accept", "*/*");
            httpPut.addHeader("Content-Type", "text/plain");
 ```
  
### Dingz config  
Define "Action URL (generic)" in Dingz  
```
get://ApiHelperHostIP:8000/itemName
```
results in API call to the helper API:
```
http://ApiHelperHostIP:8000/itemName?mac=XXXXXXXXXXX4&index=x&action=x  
```

### ready to use jar in '/out/artifacts/dingzOpenHabHelperApi_jar/'
The actual standalone jar can be found under 'releases'.

### define 'properties.config'
A config file in the same directory as the main program is required.  
For local testing place file in root directory of the project.
Name: properties.config  
  
Content:
```
exposeApiName=http://0.0.0.0:8000/
openhabHostname=http://10.0.1.x:8080
allowedDevices=F008D1C4D124,F008D1C4D125 [or ANY]
allowedIps=10.0.1.25,10.0.1.24 [or ANY]
Token=yourOpenHabApiToken
ResetMode=true
ResetValue=0
```
| property        | description                                                                                                   |
|-----------------|---------------------------------------------------------------------------------------------------------------|
| exposeApiName   | URL where the API will be exposed (0.0.0.0 -> can be accessed from anywhere in the subnet)                    |
| openhabHostname | URL or DNS alias of the OpenHAB instance incl. port                                                           |
| allowedDevices  | list of allowed devices, will be compared with query parameter "mac" from API call                            |
| allowedIps      | allowed IP addresses for incoming requests                                                                    |
| Token           | OpenHAB API token                                                                                             |
| ResetMode       | true or false, if true the state will be set to "action" query parameter and immediately back to "ResetValue" |
| ResetValue      | if "ResetMode" is true, the item value will be set to this value after every update                           |

### run configuration for local test
```
add new Configuration (+)

Name: dingzOpenHabHelperApi
Main Class: dingzOpenHabHelperApi
Working Directory: e.g. /Users/remo/Documents/workspace/dingzOpenHabHelperApi
Use classpath of module: dingzOpenHabHelperApi
```

**URLs**
```
http://localhost:8000/itemName?mac=F008D1C4D124&index=3&action=2  
http://localhost:8000/itemName?index=3&action=2  
http://localhost:8000/itemName?action=2  
```
query parameters mac & index are not mandatory  

| parameter | description                                                                                            |
|-----------|--------------------------------------------------------------------------------------------------------|
| itemName  | the itemname in OpenHAB                                                                                |
| mac       | must be in properties.config, when not in url, set allowedDevices=ANY, otherwise call will be rejected |
| index     | not used, but is sent by dingz API call                                                                |
| action    | update string for OpenHAB item                                                                         |


### build standalone jar
**setup**

Menu **File** >> **Project Structure** >> **Artifacts** >> **+** >> **JAR** >> **From modules with dependencies**  
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
exposeApiName=http://0.0.0.0:8000/
openhabHostname=http://10.0.1.x:8080
allowedDevices=F008D1C4D124,F008D1C4D125 [or ANY]
allowedIps=10.0.1.25,10.0.1.24 [or ANY]
Token=yourOpenHabApiToken
ResetMode=true
ResetValue=0
```
Adjust permissions:
```
chown -R dingzhelper:dingzhelper /usr/share/dingzOpenHabHelperApi
chmod -R 770 /usr/share/dingzOpenHabHelperApi/*
chmod -R 700 /usr/share/dingzOpenHabHelperApi/properties.config  

-rwxrwx---. 1 dingzhelper dingzhelper 8649249 Jan 17 09:31 dingzOpenHabHelperApi.jar
-rwxrwx---. 1 dingzhelper dingzhelper     159 Jan 17 10:39 dingzOpenHabHelperApiStart.sh
-rwxrwx---. 1 dingzhelper dingzhelper     134 Jan 16 16:24 dingzOpenHabHelperApiStop.sh
-rw-------. 1 dingzhelper dingzhelper     263 Jan 17 10:36 properties.config
```
  
Service (/etc/systemd/system/dingz.service) -> tested in CentOS8
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

### Windows Service -> not tested
use nssm.exe (https://nssm.cc/)  
to install service open CMD, cd to the directory where the API helper is stored  
```
new:  nssm install dingz
edit: nssm edit dingz  
```
use defaults, except:
```
Path:              C:\..your path..\dingzOpenHabHelperApi.jar
Startup directory: C:\..your path..\
Display Name:      dingz
Startup Type:      Automatic
Logon:             Local System Account
Process:           Normal
Console Window:    tick
Shutdown:          tick all
Output (stdout):   C:\..your path..\dingzOpenHabHelperApi.log
Error (stderr):    C:\..your path..\dingzOpenHabHelperApi.log
File Rotation:     Rotate File / 1000000 bytes
```

### logfile output samples
```
dd.mm.yyyy HH:mm:ss.SSS - INFO:  Jersey server starting at http://0.0.0.0:8000/
dd.mm.yyyy HH:mm:ss.SSS - INFO:  Jersey server started at http://0.0.0.0:8000/
dd.mm.yyyy HH:mm:ss.SSS - INFO:  Update item 'itemName' to value 'action' from device 'mac(index)' successfully sent.
dd.mm.yyyy HH:mm:ss.SSS - ERROR: File 'properties.config' not found.
dd.mm.yyyy HH:mm:ss.SSS - ERROR: Commands from device mac not allowed.
dd.mm.yyyy HH:mm:ss.SSS - ERROR: Update item 'itemName' to value 'action' from device 'mac(index)' failed with HTTPXXX.
dd.mm.yyyy HH:mm:ss.SSS - ERROR: OpenHAB not available.
```
