# hometownchat

Personal Expense Tracker is an application which is used to monitor personal expenses and set limits on expeniture.
This also helps in keeping track of money in home/base currency.

Application is designed to run on phones.
Min SDK = 19
Api used to get currency conversion rates: http://fixer.io/
External library used for graph is taken from android-graphview.org


Sailent features include:

1. App has accomodated 20 popular currencies of the world. User can enter income/expense in any currency. When the user logs in, he has the option 
of choosing his home currency. Balance is displayed in both home currency as well as in the default currency which is USD.

2. Set budget gives option to limit expenses, per week or per month. Limit needs to be set per category. The system gives an alert 
if the expense is about to meet the allocated budget. Notification is sent thrice. Once when 60% of the budget is reached. Another time when 90% of 
the budget is reached and when the budget limit is crossed.

3. App supports multiple accounts. User has the option to add as well as remove the account. User can select the account used for payment. When he 
enters the transaction information as per the account used for transaction, summary is displayed per account. Default mode of payment is cash. 
When viewing the account information, user can delete the transaction. 

4. A shopping list is available, with an option to add expenses upon finishing shopping. Expense will be logged as shopping mentioning the category 
shopped. User can add and remove the items to the shopping list. Items that are purchased can be checked. Checked items are striked out. It can be 
reset by unchecking. Item can be removed with the help of remove button at the end of individual items.

5.Restore Data: This is for the purpose of enabling users to retain their information when using the app on a different phone. On click of this option,
the app gets synchronized with the data on the old phone. This works only when the app is newly installed on the new device, just to ensure there are 
no multiple back ups. 





