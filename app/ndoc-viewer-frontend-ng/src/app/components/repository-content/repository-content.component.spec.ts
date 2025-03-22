import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RepositoryContentComponent } from './repository-content.component';

describe('RepositoryContentComponent', () => {
  let component: RepositoryContentComponent;
  let fixture: ComponentFixture<RepositoryContentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RepositoryContentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RepositoryContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
