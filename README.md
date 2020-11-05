# ETHER-DFS

## Summary

Ether-DFS is a simple distributed filesystem effort for our CS621 class' final project. We make use of Java RMI for RPC interfaces between client, master and minion servers. The filesystem was designed on the assumption that we are only dealing with only several minion servers. 

## Master Server

The Java RMI registry sits along with the master server process. Running the MasterServer is essential before doing anything else.

## Accessing Amazon EC2 Instances

Master, and Minion servers will likely run on multiple instances:

Instructions:

1. Head on over to awseducate.com
2. Enter login credentials:
    1. username: td66491@umbc.edu
    2. password: qwerasd- + course-number e.g (CMSC#)

3. Click on 'MY CLASSROOM', and then 'Find Service or Recently Visitied Services "EC2"

4. Go to 'Instances' located on the left and select the desired VM and hit 'Start Instance'

5. For a given instance, click "Connect" and begin the SSH Client

6. Make sure you have the security key (sitting in the misc folder)

7. Ensure to stop all instances when done


