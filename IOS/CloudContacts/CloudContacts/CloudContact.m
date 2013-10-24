//
//  CloudContact.m
//  CloudContacts
//
//  Created by Rueben Anderson on 10/17/13.
//  Copyright (c) 2013 Rueben Anderson. All rights reserved.
//

#import "CloudContact.h"

@implementation CloudContact


// Kinvey protocol method for mapping local k/v to kinvey JSON object
- (NSDictionary*)hostToKinveyPropertyMapping
{
    return @{@"entityID": KCSEntityKeyId,
             @"firstName": @"firstName",
             @"lastName": @"lastName",
             @"age": @"age",
             @"height": @"height",
             @"weight": @"weight",
             @"employed": @"employed",
             @"phoneNumber": @"phoneNumber",
             @"emailAddress": @"emailAddress",
             @"metaData": KCSEntityKeyMetadata
             };
}




@end
