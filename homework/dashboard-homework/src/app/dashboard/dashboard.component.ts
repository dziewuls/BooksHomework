import { Component, OnInit } from '@angular/core';
import { Book } from '../model/book';
import { BookHttpService } from '../service/book-http.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  providers: [BookHttpService]
})
export class DashboardComponent implements OnInit {

  private booksToView: Array<Book>;
  private categories: Array<string>;

  constructor(private bookHttpService: BookHttpService) { }

  ngOnInit() {
    this.categories = ['Computers', 'Java', 'Religion', 'Great Britain', 'History', 'Community life',
      'Business & Economics', 'Data structures', 'Political Science', 'Indonesia', 'Social Science'];
  }

  loadBooksByCategory(category: string): void {
    if (category) {
      this.bookHttpService.getBooksByCategory(category).subscribe(result => this.booksToView = result);
    }
  }
}
