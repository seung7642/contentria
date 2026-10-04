package com.contentria.api.comment

//@Entity
//@Table(name = "comments")
//class Comment(
//    @Id
//    @Column(columnDefinition = "UUID")
//    var id: UUID = UUID.randomUUID(),
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "post_id", nullable = false)
//    var post: Post,
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    var user: User? = null,
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "parent_comment_id")
//    var parent: Comment? = null,
//
//    @OneToMany(mappedBy = "parent", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
//    var replies: MutableList<Comment> = mutableListOf(),
//
//    var content: String,
//
//    var deletedAt: ZonedDateTime? = null,
//
//    @Column(nullable = false)
//    var createdAt: ZonedDateTime = ZonedDateTime.now(),
//
//    @Column(nullable = false)
//    var updatedAt: ZonedDateTime = ZonedDateTime.now()
//)