//
//  UserDefaults.h
//  
//
//  Created by Rueben Anderson on 4/16/13.
//  Copyright (c) 2013 Rueben Anderson. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface UserDefaults : NSObject

+ (void)setItem:(id)targetObject forKey:(NSString *)key;
+ (void)setNumber:(float)targetObject forKey:(NSString *)key;
+ (void)setItems:(NSDictionary *)keyValueObject;
+ (id)getItem:(NSString *)key;
+ (float)getNumber:(NSString *)key;
+ (NSDictionary *)getAllItems:(NSArray *)keyObject;

@end
