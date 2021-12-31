package com.medictime.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.medictime.entity.Medicine
import com.medictime.entity.User

data class UserMedicine(
    @Embedded
    val detailMedicine: Medicine,

    @Relation(
        parentColumn = "user_id",
        entity = User::class,
        entityColumn = "id"
    )
    val detailUser: User
)