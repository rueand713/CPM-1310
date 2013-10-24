//
//  UserDefaults.m
// 
//
//  Created by Rueben Anderson on 4/16/13.
//  Copyright (c) 2013 Rueben Anderson. All rights reserved.
//

#import "UserDefaults.h"

@implementation UserDefaults

// class method for saving a key value pair into the standard defaults
+ (void)setItem:(id)targetObject forKey:(NSString *)key
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
                                
    if (defaults)
    {
        // set the passed in item
        [defaults setObject:targetObject forKey:key];
        
        // save the data
        [defaults synchronize];
    }
}

// class method for saving a key float-value pair into the standard defaults
// int values needed to be cast to float before being set
+ (void)setNumber:(float)targetObject forKey:(NSString *)key
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    if (defaults)
    {
        // set the passed in item
        [defaults setFloat:targetObject forKey:key];
        
        // save the data
        [defaults synchronize];
    }
}

// class method for saving multiple key value pairs into the standard defaults
+ (void)setItems:(NSDictionary *)keyValueObject
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    if (defaults)
    {
        NSArray *keys = [keyValueObject allKeys];
        NSArray *values = [keyValueObject allValues];
        
        // set each item in the array
        for (int i = 0; i < [keys count]; i++)
        {
            [defaults setObject:[values objectAtIndex:i] forKey:[keys objectAtIndex:i]];
        }
        
        // save the data
        [defaults synchronize];
    }
}

// class method for retrieving a single key value from the standard defaults
+ (id)getItem:(NSString *)key
{
    id defaultsData = nil;
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    if (defaults)
    {
        // sets the defaults data to the object for returning
        defaultsData = [defaults objectForKey:key];
    }
    
    return defaultsData;
}

// class method for retriving a float value from the standard defaults
// the number may then be cast to int if int is needed
+ (float)getNumber:(NSString *)key
{
    float defaultsData = 0;
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    if (defaults)
    {
        // sets the defaults data to the object for returning
        defaultsData = [defaults floatForKey:key];
    }
    
    return defaultsData;
}

// class method for retrieving multiple key values from the standard defaults
+ (NSDictionary *)getAllItems:(NSArray *)keyObject
{
    NSMutableDictionary *requestedItems = nil;
    
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    if (defaults)
    {
        for (int i = 0; i < [keyObject count]; i++)
        {
            // fetch each object from the defaults
            id currentObject = [defaults objectForKey:[keyObject objectAtIndex:i]];
            
            // set the fetched default item to the object dictionary
            [requestedItems setObject:currentObject forKey:[keyObject objectAtIndex:i]];
        }
    }
    
    // returns the values associated with the requested keys
    return requestedItems;
}

@end
