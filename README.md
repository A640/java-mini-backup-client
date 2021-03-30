# java-mini-backup-client
Simple Java application for creating files backup. This is `client` side of app. Server side can be found [here](https://github.com/A640/java-mini-backup-server)

## Introduction

This project was created as final project for my Java classes. The goal was to create an app using only Java and JavaFX and practise some usefult programming concepts.


## Technologies

#### Front-end

- JavaFX

#### Back-end

- Java

#### Database

- MySQL database

## Practised concepts

- concurrent tasks
- task synchronization (Mutex, CountDownLatch)
- server - client architecture
- database support
- graphical UI programmed manually (without use of graphical tools)
- unit testing 


## Features

- creating backup copy of selected files on server
- faster transfers by transfering files in different processes through different sockets
- personal accounts for users
- backups can be named and you can write descriptions for them
- users can see only their own files 

## Usage
To use this project you will need `Java 9.0.1` or higher.
