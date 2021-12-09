# JIRA STORY

As a user I want to retrieve funds from an ATM.

I can only do balance request and withdrawals. 

Given the ATM has 8000 dollars in it, when a user wants to retrieve money then 
the ATM should output the left balance in the users account or error.

● The user needs to authentificate with correct PIN

● There should be possible to have overdraft functionality

There are 3 errors possible:  
ATM_ERR when ATM is empty,  
FUNDS_ERR when the account doesn't have funds,  
ACCOUNT_ERR when wrong pin was inserted  

If any error occurs the transaction should be rolledbacked in that session.