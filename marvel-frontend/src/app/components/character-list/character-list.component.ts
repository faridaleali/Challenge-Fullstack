import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subject, debounceTime, distinctUntilChanged, takeUntil, switchMap, of, catchError, startWith } from 'rxjs';
import { CharacterService } from '../../services/character.service';
import { CharacterResponse } from '../../models/character.model';
import { CharacterDetailComponent } from '../character-detail/character-detail.component';

@Component({
  selector: 'app-character-list',
  templateUrl: './character-list.component.html',
  styleUrls: ['./character-list.component.scss']
})
export class CharacterListComponent implements OnInit, OnDestroy {
  characters: CharacterResponse[] = [];
  loading = false;
  searchControl = new FormControl('');
  private destroy$ = new Subject<void>();

  constructor(
    private characterService: CharacterService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.setupSearch();
    // No need to call loadCharacters() because setupSearch() already loads them with startWith('')
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private setupSearch(): void {
    this.searchControl.valueChanges
      .pipe(
        startWith(''),
        debounceTime(300),
        distinctUntilChanged(),
        takeUntil(this.destroy$),
        switchMap(query => {
          this.loading = true;
          if (query && query.trim()) {
            return this.characterService.searchCharacters(query.trim()).pipe(
              catchError(error => {
                this.handleError('Error searching characters', error);
                return of([]);
              })
            );
          } else {
            return this.characterService.getCharacters().pipe(
              catchError(error => {
                this.handleError('Error loading characters', error);
                return of([]);
              })
            );
          }
        })
      )
      .subscribe(characters => {
        this.characters = characters;
        this.loading = false;
      });
  }

  private loadCharacters(): void {
    this.loading = true;
    this.characterService.getCharacters().pipe(
      takeUntil(this.destroy$),
      catchError(error => {
        this.handleError('Error loading characters', error);
        return of([]);
      })
    ).subscribe(characters => {
      this.characters = characters;
      this.loading = false;
    });
  }

  openCharacterDetail(character: CharacterResponse): void {
    const dialogRef = this.dialog.open(CharacterDetailComponent, {
      width: '600px',
      maxWidth: '90vw',
      maxHeight: '90vh',
      data: character,
      panelClass: 'character-detail-dialog'
    });

    dialogRef.afterClosed().subscribe(result => {
      // Handle any result if needed
    });
  }

  clearSearch(): void {
    this.searchControl.setValue('');
  }

  refresh(): void {
    this.searchControl.setValue('');
    this.loadCharacters();
  }

  private handleError(message: string, error: any): void {
    console.error(message, error);
    let errorMessage = message;
    
    if (error.status === 0) {
      errorMessage = 'Unable to connect to the server. Please check your connection.';
    } else if (error.status === 401) {
      errorMessage = 'Authentication failed. Please log in again.';
    } else if (error.error?.message) {
      errorMessage = error.error.message;
    }

    this.snackBar.open(errorMessage, 'Close', {
      duration: 5000,
      panelClass: ['error-snackbar']
    });
  }
}