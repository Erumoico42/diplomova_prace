!COMMENT!
Enter your description of the rulebase here.
!END_COMMENT!

TypeOfDescription=linguistic
InfMethod=Fuzzy_Approximation-functional
DefuzzMethod=SimpleCenterOfGravity
UseFuzzyFilter=false

NumberOfAntecedentVariables=2
NumberOfSuccedentVariables=1
NumberOfRules=20

AntVariable1
 name=dSpeed
 settings=new
 context=<-7,-5,0,5,7>
 discretization=301
 discretization_left=301
 UserTerm
  name=very slow
  type=trapezoid
  parameters= -7 -7 -6.3 -3.5
 End_UserTerm
 UserTerm
  name=slow
  type=trapezoid
  parameters= -6.3 -3.5 -2.8 0
 End_UserTerm
 UserTerm
  name=equal
  type=triang
  parameters= -2.8 0 2.8
 End_UserTerm
 UserTerm
  name=fast
  type=trapezoid
  parameters= 0 2.8 3.5 6.3
 End_UserTerm
 UserTerm
  name=very fast
  type=trapezoid
  parameters= 3.5 6.3 7 7
 End_UserTerm
End_AntVariable1

AntVariable2
 name=distance
 settings=new
 context=<0,5,10>
 discretization=301
 UserTerm
  name=too close
  type=trapezoid
  parameters= 0 0 0.2 2
 End_UserTerm
 UserTerm
  name=close
  type=trapezoid
  parameters= 0.5 2 3 4.5
 End_UserTerm
 UserTerm
  name=medi
  type=trapezoid
  parameters= 3 4.5 6 8
 End_UserTerm
 UserTerm
  name=far
  type=trapezoid
  parameters= 6 8 10 10
 End_UserTerm
End_AntVariable2

SucVariable1
 name=acceleration
 settings=new
 context=<-6,-2,0,2,6>
 discretization=301
 discretization_left=301
 UserTerm
  name=rapid down
  type=trapezoid
  parameters= -6 -6 -5.4 -4.71
 End_UserTerm
 UserTerm
  name=zero acc
  type=trapezoid
  parameters= -2.4 -0.6 0.7 2.8
 End_UserTerm
 UserTerm
  name=down
  type=trapezoid
  parameters= -6 -4.71 -3.6 -2.4
 End_UserTerm
 UserTerm
  name=up
  type=trapezoid
  parameters= 0.7 2.8 4.2 5.6
 End_UserTerm
 UserTerm
  name=max up
  type=trapezoid
  parameters= 4.2 5.6 6 6
 End_UserTerm
 UserTerm
  name=light down
  type=triang
  parameters= -3.43 -2.4 -0.6
 End_UserTerm
End_SucVariable1

RULES
 "very fast" "close" | "down"
 "fast" "close" | "light down"
 "equal" "close" | "zero acc"
 "slow" "close" | "up"
 "very slow" "close" | "up"
 "very fast" "far" | "zero acc"
 "fast" "far" | "zero acc"
 "equal" "far" | "up"
 "slow" "far" | "max up"
 "very slow" "far" | "max up"
 "very fast" "medi" | "light down"
 "fast" "medi" | "zero acc"
 "equal" "medi" | "zero acc"
 "slow" "medi" | "up"
 "very slow" "medi" | "max up"
 "very fast" "too close" | "rapid down"
 "fast" "too close" | "down"
 "equal" "too close" | "down"
 "slow" "too close" | "zero acc"
 "very slow" "too close" | "up"
END_RULES
