config->Constants - Class for defining tool configuraiton level variable
config->ActionKeywords - Class for core Selenium functions which map to action keyword in test suite excel

executionEngine->DriverScript - class having main function
executionEngine->DriverMembers - Class encapsulating global level variables

dataEngine-> folder location for sample test suit excel


version 1.1
1. Added skeleton for iterative call of a test cases and providing data from a data feeder file
2. Handled ElementNotInteratableException to perform page scroll and try the specified action. Particularly helpful currently in ITR 2.0 automation

version 1.2
1. Code fix for test case execution still continued when a step failed within a functional block