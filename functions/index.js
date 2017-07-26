// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });


/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

/**
 * Triggers when a user gets a new follower and sends a notification.
 *
 * Followers add a flag to `/followers/{followedUid}/{followerUid}`.
 * Users save their device notification tokens to `/users/{followedUid}/notificationTokens/{notificationToken}`.
 */
exports.sendNewEventNotification2 = functions.database.ref('/events/{newEventId}').onWrite(event => {
  const newEventId = event.params.newEventId;
  
  const newEvent = event.data.val();
 
  // If event delete??
  if (!event.data.val()) {
    return console.log("evento cancellato");
  }
  console.log('Nuovo evento creato:', newEventId);
  console.log('myiooo', newEvent);

  // Get the list of device notification tokens.
  const getDeviceTokensPromise = admin.database().ref(`/users/G6n9EswU06ccXXx4U0Ai27RekvV2/devices`).once('value');

  // Get event detail
  //const getEventDetail = admin.database().ref(`/events/${newEventId}`);

  return Promise.all([getDeviceTokensPromise]).then(results => {
    const tokensSnapshot = results[0];
    //const eventDetail = result[1];

    // Check if there are any device tokens.
    if (!tokensSnapshot.hasChildren()) {
      return console.log('There are no notification tokens to send to.');
    }
    console.log('There are', tokensSnapshot.numChildren(), 'tokens to send notifications to.');
    //console.log('Fetched follower profile', follower);

    // Notification details.
    const payload = {
      notification: {
        title: 'Nuovo Evento creato',
        body: `${newEvent.name} per il gruppo ${newEvent.channelName}`,
       // icon: follower.photoURL,
        sound : 'default'
      }
    };

    // Listing all tokens.
    const tokens = Object.keys(tokensSnapshot.val());

    // Send notifications to all tokens.
    return admin.messaging().sendToDevice(tokens, payload).then(response => {
      // For each message check if there was an error.
      const tokensToRemove = [];
      response.results.forEach((result, index) => {
        const error = result.error;
        if (error) {
          console.error('Failure sending notification to', tokens[index], error);
          // Cleanup the tokens who are not registered anymore.
          if (error.code === 'messaging/invalid-registration-token' ||
              error.code === 'messaging/registration-token-not-registered') {
            tokensToRemove.push(tokensSnapshot.ref.child(tokens[index]).remove());
          }
        }
      });
      return Promise.all(tokensToRemove);
    });
  });
});
