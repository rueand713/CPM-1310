//
//  DetailsViewController.h
//  CloudContacts
//
//  Created by Rueben Anderson on 10/18/13.
//  Copyright (c) 2013 Rueben Anderson. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SQLiteManager.h"

@interface DetailsViewController : UIViewController <UITextFieldDelegate>
{
    IBOutlet UIButton *backButton;
    IBOutlet UIButton *saveButton;
    IBOutlet UIButton *closeKeyboard;
    
    IBOutlet UITextField *firstName;
    IBOutlet UITextField *lastName;
    IBOutlet UITextField *age;
    IBOutlet UITextField *phoneNumber;
    IBOutlet UITextField *emailAddress;
    IBOutlet UITextField *height;
    IBOutlet UITextField *weight;
    IBOutlet UITextField *employed;
    
    UITextField *currentTextField;
    
    SQLiteManager *sqlManager;
}

@property (nonatomic, strong) NSString *entityID;
@property (nonatomic, strong) NSDate *metaData;
@property (nonatomic, strong) NSString *firstNameValue;
@property (nonatomic, strong) NSString *lastNameValue;
@property (nonatomic, strong) NSString *ageValue;
@property (nonatomic, strong) NSString *phoneNumberValue;
@property (nonatomic, strong) NSString *emailAddressValue;
@property (nonatomic, strong) NSString *heightValue;
@property (nonatomic, strong) NSString *weightValue;
@property (nonatomic, strong) NSString *employedValue;
@property int idPosition;

-(IBAction)onClick:(id)sender;
-(void)saveLocal;

@end
