# Firmware Test Software Project

## Overview

This repository contains the source code for a firmware test software project developed in Java using the Eclipse IDE. 
The project utilizes JavaFX for the graphical user interface (GUI) and the eOCSF framework for client-server communication. 
The primary goal of this software is to test the functionality of a microcontroller managing a system, following the specifications outlined in the attached API documentation.

## Project Components

### 1. Graphical User Interface (GUI)

- Developed using JavaFX within the Eclipse IDE.
- GUI allows users to connect to the microcontroller, run tests, and view results.
- Full functionality for presenting test results (pass or fail) is currently a work in progress.

### 2. Connection Establishment

- Successful implementation of socket communication for establishing a connection with the microcontroller.
- Work in progress on implementing the DISCONNECT COM API for proper disconnection.

### 3. Packet Saver

- Substantial progress in creating a packet saver for smooth streaming of byte arrays based on company-provided packet protocols.
- Further refinements are required for optimal performance.

### 4. File Reporting System

- Challenges encountered in implementing the file reporting system and updating tables based on GET test results.
- Ongoing work to refine and complete these functions.

## How to Use

1. Clone the repository to your local machine.
2. Open the project in Eclipse IDE.
3. Build and run the project.
4. Use the GUI to connect to the microcontroller and run tests.

## Project Status

The project is behind schedule, and the initial deadline will not be met. 

