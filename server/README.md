# 1. Installation

## 1.1 REST API Setup

Set up an AWS server with an Ubuntu distro then do the following if not already done:

-  Install python3
-  Install pip3
-  Install virtualenv
```
sudo apt install virtualenv
```
-  Create and activate virtualenv with the following commands
```
virtualenv env
source env/bin/activate
```
-  Install django & djangorestframework + dependencies with the following commands
```
pip3 install django
pip3 install djangorestframework
pip3 install psycopg2
```
-  Install MQTT for Python
```
pip3 install paho-mqtt
```
-  Install PSQL locally
```
sudo apt install postgresql
```
-  Start Docker Postgres container on port 25432
-  Ensure crendentials match to connect to server by using the following example commands
```
CREATE USER petprototype WITH password '123123';
CREATE DATABASE petprototype;
```

-  Migrate the models
```
python3 manage.py migrate
```
-  Run server
```
python3 manage.py runserver
```
- Run the MQTT service
```
python3 manage.py runmqtt
```
