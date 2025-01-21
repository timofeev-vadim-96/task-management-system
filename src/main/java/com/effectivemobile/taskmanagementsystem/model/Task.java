package com.effectivemobile.taskmanagementsystem.model;

import com.effectivemobile.taskmanagementsystem.util.TaskPriority;
import com.effectivemobile.taskmanagementsystem.util.TaskStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString(of = {"id", "title", "description", "comments"})
@EqualsAndHashCode(of = {"id"})
@Builder
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Setter
    @Column(name = "title")
    private String title;

    @Setter
    @Column(name = "description")
    private String description;

    @Setter
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "task", cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    private List<Comment> comments;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private TaskPriority priority;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private AppUser author;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "implementor_id")
    private AppUser implementor;
}
