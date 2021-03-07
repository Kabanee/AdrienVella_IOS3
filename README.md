Adrien Vella IOS 3

Mobile Application Development

Introduction :

I did not succeed the project at all. The code you will find compile but it will crash after login in. 
I will still explain how i would have done every aspects of the project but it is not/not correctly implemented.


- Explain how you ensure user is the right one starting the app

When login in, a password is asked. This password is by default the last name. At the 1st connection, it is asked to 
change the password (with restrictions)

- How do you securely save user's data on your phone ?

User's data is encrypted with a key-based encryption. We also add some salt to avoid a decryption by bruteforce.
We get the key with the user's data. Password is stored in database (encrypted with a SHA-1 and salt).

- How did you hide the API url ?

