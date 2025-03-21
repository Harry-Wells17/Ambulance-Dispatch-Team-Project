import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { LoginService } from 'app/login/login.service';
import { Title } from '@angular/platform-browser';

export interface Call {
  cat: 'CAT 1' | 'CAT 2' | 'CAT 3' | 'CAT 4' | 'CAT 5';
  title: string;
  time: number;
  description: string;
}

let examples: Call[] = [
  {
    cat: 'CAT 2',
    time: Date.now() - Math.random() * 1000000,
    title: 'Query Loss Of Consciousness',
    description: 'Head Injury, fall from 1m+, confirmed intoxication, new onset confusion.',
  },
  {
    cat: 'CAT 5',
    time: Date.now() - Math.random() * 1000000,
    title: 'Generator Needed',
    description: 'Treatment Center 2 generator has failed and needs replacing asap',
  },
  {
    cat: 'CAT 1',
    time: Date.now() - Math.random() * 1000000,
    title: 'CARDIAC ARREST',
    description: 'In progress arrest, no CBRN(e) concerns, no tramua, HCP aware, ALS in progress.',
  },
  {
    cat: 'CAT 4',
    time: Date.now() - Math.random() * 1000000,
    title: 'Grazed Knee',
    description: 'Patient self presented to TC1, grazed knee, no other injuries, conscious and breathing.',
  },
  {
    cat: 'CAT 1',
    time: Date.now() - Math.random() * 1000000,
    title: 'Query Lateral ST elevated Myocardial Infarction',
    description: 'Chest pain, new onset confusion, patient is maintaining own airway, ECG has been sent to HCP.',
  },
  {
    cat: 'CAT 3',
    time: Date.now() - Math.random() * 1000000,
    title: 'Query Fractured Femur',
    description: 'No signs of shock, patient is alert and breathing, no other injuries.',
  },
  {
    cat: 'CAT 5',
    time: Date.now() - Math.random() * 1000000,
    title: 'Welfare Check for Foot Team 2',
    description: 'Welfare check due to foot team 2 attending a category 1 call.',
  },
  {
    cat: 'CAT 2',
    time: Date.now() - Math.random() * 1000000,
    title: 'Query Stroke',
    description: 'Patient has new onset confusion and is FAST positive.',
  },
];
@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;

  list: Call[] = [];

  private readonly destroy$ = new Subject<void>();

  constructor(
    private accountService: AccountService,
    private router: Router,
    private loginService: LoginService,
    private titleService: Title
  ) {}

  ngOnInit(): void {
    this.titleService.setTitle(`AmbuDispatch`);

    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));

    let prev = -1;

    let genNew = () => {
      let ran = Math.floor(Math.random() * examples.length);
      while (ran === prev) {
        ran = Math.floor(Math.random() * examples.length);
      }
      const callNext = examples[ran];

      this.list = [callNext, ...this.list];

      if (this.list.length > 5) {
        this.list = this.list.slice(0, 5);
      }

      setTimeout(genNew, 1000 + 4000 * Math.random());
    };
    genNew();
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  logout(): void {
    this.loginService.logout();
    this.router.navigate(['']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
