//
//  DetailsViewController.m
//  CloudContacts
//
//  Created by Rueben Anderson on 10/18/13.
//  Copyright (c) 2013 Rueben Anderson. All rights reserved.
//

#import "DetailsViewController.h"

@interface DetailsViewController ()

@end

@implementation DetailsViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    sqlManager = [[SQLiteManager alloc] initWithDetails:@"CloudContacts.db" table:@"CloudContacts"];
	
    firstName.text = self.firstNameValue;
    lastName.text = self.lastNameValue;
    age.text = self.ageValue;
    height.text = self.heightValue;
    weight.text = self.weightValue;
    phoneNumber.text = self.phoneNumberValue;
    emailAddress.text = self.emailAddressValue;
    employed.text = self.employedValue;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(IBAction)onClick:(id)sender
{
    UIButton *button = (UIButton *) sender;
    
    if (button != nil)
    {
        if (button.tag == 0)
        {
            // close the database
            [sqlManager closeDatabase];
            
            // dismiss the view
            [self dismissViewControllerAnimated:YES completion:^{
                
            }];
        }
        else if (button.tag == 1)
        {
            // save data here
            [self saveLocal];
        }
        else if (button.tag == 2)
        {
            // hide the keyboard and resign the first responder
            [closeKeyboard setHidden:YES];
            
            [currentTextField resignFirstResponder];
        }
    }
}


// method for updating the values in the local sql table
-(void)saveLocal
{
    
    // create a where string
    NSString *whereString = [NSString stringWithFormat:@"_id = %i", self.idPosition];
    
    NSString *empStr = [employed.text lowercaseString];
    
    // set the integer representation of bool string for true
    int employedInt = 0;
    
    // check if the employee value string is true or false
    // set the employedInt to 1 if false
    if ([empStr isEqualToString:@"false"])
    {
        employedInt = 1;
    }
    
    // set the contact data to dictionary for updating
    NSDictionary *columnNames = @{
                                  @"entityID": [NSString stringWithFormat:@"\"%@\"", self.entityID],
                                  @"firstName": [NSString stringWithFormat:@"\"%@\"", firstName.text],
                                  @"lastName": [NSString stringWithFormat:@"\"%@\"", lastName.text],
                                  @"phoneNumber": [NSString stringWithFormat:@"\"%@\"", phoneNumber.text],
                                  @"emailAddress": [NSString stringWithFormat:@"\"%@\"", emailAddress.text],
                                  @"age": age.text,
                                  @"height": height.text,
                                  @"weight": weight.text,
                                  @"employed": [NSString stringWithFormat:@"%i", employedInt]
                                  };
    
    [sqlManager update:columnNames where:whereString];
    
}

- (void)textFieldDidBeginEditing:(UITextField *)textField
{
    // unhide the button to close the keyboard
    [closeKeyboard setHidden:NO];
    
    // set reference to the current textfield
    currentTextField = textField;
}


@end
