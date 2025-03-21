import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { LoginService } from 'app/login/login.service';

@Component({
  selector: 'jhi-footer',
  templateUrl: './footer.component.html',
  standalone: true,
  imports: [RouterModule],
})
export class FooterComponent {
  constructor() {
    let tag = document.getElementsByTagName('html')[0];
    if (tag) {
      let size = window.localStorage.getItem('text-size') ?? '15';
      let parsed = parseInt(size, 10) ?? 15;
      if (parsed < 4 || parsed > 25) {
        parsed = 15;
      }
      tag.style.fontSize = parsed + 'px';
    }
  }

  changeFont(x: number) {
    let tag = document.getElementsByTagName('html')[0];
    if (tag) {
      let textSize = x + parseInt(tag.style.fontSize.replace('px', ''), 10);
      window.localStorage.setItem('text-size', textSize.toString());
      if (textSize < 4 || textSize > 25) {
        textSize = 15;
      }
      tag.style.fontSize = (textSize ?? 15) + 'px';
    }
  }
}
