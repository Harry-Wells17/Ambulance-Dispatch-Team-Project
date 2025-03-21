import { Component } from '@angular/core';
import { RouterLink, RouterModule } from '@angular/router';
import { NgForOf, NgIf } from '@angular/common';
import { LogType } from 'app/entities/enumerations/log-type.model';

@Component({
  standalone: true,
  selector: 'userlogs-component',
  templateUrl: './userlogs.component.html',
  imports: [RouterLink, RouterModule, NgForOf, NgIf],
})
export class UserLogsComponent {}
