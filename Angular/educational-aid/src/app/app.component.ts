import { Component } from '@angular/core';
import { AngularFireDatabase, AngularFireList } from 'angularfire2/database';

class Lesson {
  constructor(public image, public name, public price, public visibility) {}
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent {
  title = 'app';
  public lessons: AngularFireList<Lesson[]>;
    constructor(db: AngularFireDatabase) {
        this.lessons = db.list('/Lessons');
    }
}
