# Vaadin Swing demo

A small demo app using:

- Vaadin
- Spring Boot
- Project Reactor
- Swing

# Purpose
This is a small technology demo showing how to provide a desktop app that is tightly integrated with a web app.
My house has a large number of network connected devices including desktops, laptops, Chromebooks, iOS, and Android
devices. This is the potential start of using their assorted inputs and outputs cooperatively when convenient.

# Limitations
This runs with a particular set of old versions of Java, Spring Boot, and Vaadin.
Attempts to move beyond Java 8 didn't prove as trivial as I hoped.

# Running

`mvn clean package spring-boot:run`

# Origin
This project started from this [Vaadin Chat](https://github.com/marcushellberg/vaadin-chat).

# Potential Next Steps
- Better installation
- Bug fixes
- Better UI
  - Add system tray support
  - Support service discovery mechanisms
- Remote text pad
- Remote terminal
- Remote document display
  - digital signage
  - information radiator
- Task specific input -- like a Stream Deck
