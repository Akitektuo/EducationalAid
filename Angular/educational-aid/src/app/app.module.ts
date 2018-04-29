import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations'
import { 
  MatCardModule, 
  MatButtonModule, 
  MatSelectModule,
  MatToolbarModule,
  MatListModule
} from '@angular/material'
import { AngularFireModule } from 'angularfire2';
import { AngularFireDatabaseModule } from 'angularfire2/database';

import { AppComponent } from './app.component';
import { environment } from '../environments/environment.prod';


@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MatCardModule,
    MatButtonModule,
    MatSelectModule,
    MatToolbarModule,
    MatListModule,
    AngularFireDatabaseModule,
    AngularFireModule.initializeApp(environment.firebase, "educational-aid")
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
