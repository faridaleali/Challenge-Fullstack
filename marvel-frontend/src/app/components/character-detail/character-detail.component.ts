import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CharacterResponse } from '../../models/character.model';

@Component({
  selector: 'app-character-detail',
  templateUrl: './character-detail.component.html',
  styleUrls: ['./character-detail.component.scss']
})
export class CharacterDetailComponent {
  
  constructor(
    public dialogRef: MatDialogRef<CharacterDetailComponent>,
    @Inject(MAT_DIALOG_DATA) public character: CharacterResponse
  ) {}

  onClose(): void {
    this.dialogRef.close();
  }

  getStatColor(value: number): string {
    if (value === 0) return '#ccc';
    if (value < 10) return '#ff9800';
    if (value < 50) return '#2196f3';
    return '#4caf50';
  }

  getStatWidth(value: number): string {
    if (value === 0) return '5%';
    // Normalize to a reasonable scale (max 100)
    const normalizedValue = Math.min(value, 100);
    return `${Math.max(normalizedValue, 5)}%`;
  }
}