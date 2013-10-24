//
//  ViewController.h
//  CloudContacts
//
//  Created by Rueben Anderson on 10/15/13.
//  Copyright (c) 2013 Rueben Anderson. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <KinveyKit/KinveyKit.h>
#import "SQLiteManager.h"
#import "CloudContact.h"
#import "UserDefaults.h"
#import "DateFormatter.h"

@interface ViewController : UIViewController <UITableViewDataSource, UITableViewDelegate>
{
    KCSAppdataStore *kinveyStore;
    SQLiteManager *sqlManager;
    KCSUser *appUser;
    NSArray *kinveyData;
    NSDate *lmdKinvey;
    NSDictionary *queryResults;
    IBOutlet UITableView *contactTable;
    BOOL refreshTable;
    NSDictionary *sqlData;
    NSDate *lastModifiedDate;
    DateFormatter *formatter;
    
    IBOutlet UIButton *newButton;
    IBOutlet UIButton *syncButton;
    
}

-(IBAction)onClick:(id)sender;

-(void)retrieveKinveyData;
-(void)synchronizeData;
-(void)loadLocal;
-(void)setupStore;
-(void)setupSQL;
-(NSDate *)setNewLastDate:(NSString *)date;

@end
