import { Component, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import {RouterModule, Router, ActivatedRoute } from '@angular/router';
import {ContactService} from '../services/contact.service';
import { OnInit } from '@angular/core';
import { Contact } from '../model/contact.interface';
import { Observable } from 'rxjs';



@Component({
  selector: 'app-contact-form',
  standalone: true,
  imports: [RouterModule, ReactiveFormsModule],
  templateUrl: './contact-form.component.html',
  styleUrl: './contact-form.component.css'
})
export default class ContactFormComponent implements OnInit {

  private fb = inject(FormBuilder);
  private contactService = inject(ContactService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  form?: FormGroup;
  contact?: Contact;
  errors: string[] = [];

  ngOnInit(): void {
   const id = this. route.snapshot.paramMap.get('id');
   
   if (id){
    this.contactService.get(parseInt(id)).subscribe(contact => {
      this.contact = contact;
      this.form = this.fb.group({
        name: [contact.name, Validators.required],
        email: [contact.email, Validators.required, Validators.email]
      });
    });
    
  } else {
    this.form = this.fb.group({
      name: ['', Validators.required],
      email: ['', Validators.required, Validators.email]
    });
  }
}

  save (){
    if (this.form?.invalid){
      this.form.markAllAsTouched();
      return;
    }

    const contactForm = this.form!.value;
    let request: Observable<Contact>;

    if (this.contact){
      request = this.contactService.update(this.contact.id, contactForm);
    }else {
      request = this.contactService.create(contactForm);
    }

    request
    .subscribe({
      next: () => {
      this.errors = [];
      this.router.navigate(['/']);
     },
     error: response => {
        this.errors = response.error.errors;
      }
    })
  }
}

