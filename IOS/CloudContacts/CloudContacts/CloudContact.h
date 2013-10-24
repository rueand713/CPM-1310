//
//  CloudContact.h
//  CloudContacts
//
//  Created by Rueben Anderson on 10/17/13.
//  Copyright (c) 2013 Rueben Anderson. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <KinveyKit/KinveyKit.h>

@interface CloudContact : NSObject <KCSPersistable>

// Kinvey specific fields
@property (nonatomic, strong) NSString *entityID;
@property (nonatomic, strong) KCSMetadata *metaData;

// Object property fields
@property (nonatomic, strong) NSString *firstName;
@property (nonatomic, strong) NSString *lastName;
@property (nonatomic, strong) NSString *phoneNumber;
@property (nonatomic, strong) NSString *emailAddress;
@property (nonatomic) int age;
@property (nonatomic) int height;
@property (nonatomic) int weight;
@property (nonatomic) BOOL employed;

@end
