package com.entity;
import jakarta.persistence.*;import java.time.LocalDate;
@Entity @Table(name="magazine_issues") public class MagazineIssue{
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)private Long id;
 @Column(nullable=false,unique=true,length=80)private String demoKey;
 @Column(nullable=false,length=180)private String title;
 @Column(length=80)private String volume;
 private LocalDate publicationDate;
 @Column(columnDefinition="TEXT")private String description;
 @Column(length=500)private String coverUrl;
 @Column(length=500)private String pdfUrl;
 @Column(nullable=false)private boolean published;
 @Column(nullable=false)private boolean illustrative;
 public MagazineIssue(){}public MagazineIssue(String key,String title,String volume,LocalDate date,String desc,String cover,String pdf,boolean pub,boolean demo){demoKey=key;this.title=title;this.volume=volume;publicationDate=date;description=desc;coverUrl=cover;pdfUrl=pdf;published=pub;illustrative=demo;}
 public Long getId(){return id;}public void setId(Long v){id=v;}public String getDemoKey(){return demoKey;}public String getTitle(){return title;}public String getVolume(){return volume;}public LocalDate getPublicationDate(){return publicationDate;}public String getDescription(){return description;}public String getCoverUrl(){return coverUrl;}public String getPdfUrl(){return pdfUrl;}public boolean isPublished(){return published;}public boolean isIllustrative(){return illustrative;}public void setPublished(boolean b){published=b;}
}
